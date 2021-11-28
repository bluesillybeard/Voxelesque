package engine.gl33.render;

import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Texture;
import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.gpu.GPUBlock;
import engine.multiplatform.gpu.GPUChunk;
import engine.multiplatform.model.CPUMesh;
import game.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Map;

public class GL33Chunk implements GPUChunk, Comparable<GL33Chunk>{
    private final Vector3i pos;
    private GPUBlock[][][] blocks;
    private final int size;
    private GL33Entity[] chunkModel;
    private boolean canRender;
    private final Vector3f cameraPos;

    private ArrayList<CPUMeshBuilder> chunkModels;
    private ArrayList<ShaderTexture> shaderTextures;

    public boolean taskRunning;
    public boolean taskScheduled;

    public GL33Chunk(int size, GPUBlock[][][] blocks, int xPos, int yPos, int zPos, Vector3f cameraPos){
        if(blocks.length != size || blocks[0].length != size || blocks[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
        this.canRender = false;
        this.taskRunning = false;
        this.taskScheduled = false;
        this.size = size;
        this.pos = new Vector3i(xPos, yPos, zPos);
        this.cameraPos = cameraPos;
    }

    public void setData(GPUBlock[][][] blocks){
        if(blocks != null && (blocks.length != this.size || blocks[0].length != this.size || blocks[0][0].length != this.size)){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + this.size + "," +
                    " but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
    }
    public void setBlock(GPUBlock block, int x, int y, int z){
        blocks[x][y][z] = block;
    }
    public void render(){
        if(!canRender) return; //don't render if it can't
        for(GL33Entity entity: chunkModel){
            entity.render(); //the entities positions are already set to the right place in the build method
        }
    }

    /**
     * clears the vertex data from the GPU.
     */
    public void clearFromGPU(){
        if(this.chunkModel != null) {
            for (GL33Entity entity : this.chunkModel) {
                entity.getModel().mesh.cleanUp();//DON'T clear the texture.
            }
        }
    }

    public boolean sendToGPU(){
        if(taskRunning || taskScheduled){
            return false;
        } else if(chunkModels != null && chunkModels.size() > 0){
            int modelsAdded = 0;
            if(canRender)clearFromGPU();
            ArrayList<GL33Entity> model = new ArrayList<>();
            for (int i = 0; i < shaderTextures.size(); i++) {
                CPUMesh mesh = chunkModels.get(i).getMesh();
                if(mesh.indices.length > 0) {
                    modelsAdded++;
                    GL33Entity entity = new GL33Entity(new GL33Mesh(mesh), shaderTextures.get(i).shader, shaderTextures.get(i).texture);
                    entity.setPosition(this.pos.x * this.size * 0.288675134595f, this.pos.y * this.size * 0.5f, this.pos.z * this.size * 0.5f);
                    entity.setScale(1, 1, 1);
                    model.add(entity);
                }
            }
            chunkModels = null;
            shaderTextures = null;
            this.chunkModel = model.toArray(new GL33Entity[0]);
            this.canRender = true;
            return modelsAdded > 0;
        }
        return false;
    }

    /**
     *
     * @param chunks the map of chunk positions to chunk objects to get adjacent chunks from
     */
    public void build(Map<Vector3i, GL33Chunk> chunks) {
        if(taskRunning) {
            System.err.println("Chunk attempted to build multiple times at once:" + this);
            return;
        }
        taskRunning = true;
        /*
        an overview of how chunk building works:
        initialize a list of shaders and models

        for each block:
           get its block model
           if we don't already have its shader:
               add another shader and model to the list
           create a copy of the blocks model (not the chunk model)
           remove the removable indices based on the blocks around it
           add that block model to the chunk model

         */
        chunkModels = new ArrayList<>();
        shaderTextures = new ArrayList<>();
        ShaderTexture TSP = new ShaderTexture();

        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                for (int z = 0; z < blocks[x][y].length; z++) {
                    GPUBlock block = blocks[x][y][z];
                    if (block == null || block.getTexture() == null || block.getShader() == null) continue; //skip rendering this block if it is null (void)
                    GL33Shader program = (GL33Shader)block.getShader();
                    GL33Texture texture = (GL33Texture)block.getTexture();
                    int shaderTextureIndex = shaderTextures.indexOf(TSP.s(program).t(texture));
                    if (shaderTextureIndex == -1) {
                        shaderTextureIndex = chunkModels.size();
                        shaderTextures.add(new ShaderTexture(program, texture));
                        chunkModels.add(new CPUMeshBuilder());
                    }
                    //cloning, index removal, and vertex position modification done within the BlockMeshBuilder
                    chunkModels.get(shaderTextureIndex).addBlockMeshToChunk(block.getMesh(), x, y, z, this.getBlockedFaces(x, y, z, chunks));
                }
            }
        }
        taskScheduled = false;
        this.taskRunning = false;
    }


    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    private byte getBlockedFaces(int x, int y, int z, Map<Vector3i, GL33Chunk> chunks){
        byte blockedFaces = 0;

        for(int i=0; i < 5; i++){

            int xM = switch(i){
                case 3 -> x-1;
                case 4 -> x+1;
                default -> x;
            };

            int yM = switch(i){
                case 0 -> y+1;
                case 1 -> y-1;
                default -> y;
            };

            int zM;
            if (i == 2) {
                zM = (z + x & 1) * -2 + 1 + z; //I don't know how I figured this out, but I did.
            } else {
                zM = z;
            }
            GL33Chunk toUse = this;

            //[(-1, 0, 0), (0, -1, 0), (0, 0, -1), (+1, 0, 0), (0, +1, 0), (0, 0, +1)]
            if(xM<0){ //-1, 0, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x-1, pos.y, pos.z));
                else toUse = null;
                xM = size-1;
            }
            else if(xM>this.size-1){//+1, 0, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x+1, pos.y, pos.z));
                else toUse = null;

                xM = 1;
            }
            else if(yM<0) {//0, -1, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y-1, pos.z));
                else toUse = null;
                yM  = size-1;
            }
            else if(yM>this.size-1){ //0, +1, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y+1, pos.z));
                else toUse = null;
                yM  = 1;
            }
            else if(zM<0) { //0, 0, -1
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y, pos.z-1));
                else toUse = null;
                zM = size-1;
            }
            else if(zM>this.size-1){ //0, 0, +1
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y, pos.z+1));
                else toUse = null;
                zM = 1;
            }
            if(toUse == null){
                if(chunks != null)blockedFaces |= (1 << i); //if the chunk doesn't exist yet, assume it's blocked, unless it wasn't given adjacent chunks, in which case assume it isn't blocked.
                continue;
            }
            CPUMesh mesh = toUse.getBlock(xM, yM, zM).getMesh();

            if (mesh == null || mesh.blockedFaces == 0) continue; //skip if that mesh doesn't block faces
            blockedFaces |= (mesh.blockedFaces & (1 << i)); //add the blocked face to the bit field.
        }
        return blockedFaces;//blockedFaces;
    }

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (This should absolutely under no circumstances ever happen. Not in all time and space should this value ever be returned by this function)
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }

    public Vector3i getPosition(){
        return this.pos;
    }

    public GPUBlock getBlock(int x, int y, int z){
        return this.blocks[x][y][z];
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
     * for all {@code x} and {@code y}.  (This
     * implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
     * all {@code z}.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(GL33Chunk o) {
        return (int)(getChunkWorldPos(this.pos, this.size).distance(cameraPos) - getChunkWorldPos(o.pos, o.size).distance(cameraPos));
    }

    public static Vector3f getChunkWorldPos(Vector3i chunkPos, int chunkSize){
        return new Vector3f((chunkPos.x+0.5f)*(chunkSize*0.288675134595f), (chunkPos.y+0.5f)*(chunkSize*0.5f), (chunkPos.z+0.5f)*(chunkSize*0.5f));
    }


    private static class ShaderTexture{
        public GL33Shader shader;
        public GL33Texture texture;

        public ShaderTexture(){

        }
        public ShaderTexture(GL33Shader s, GL33Texture t){
            this.shader = s;
            this.texture = t;
        }
        public ShaderTexture s(GL33Shader s){
            this.shader = s;
            return this;
        }
        public ShaderTexture t(GL33Texture s){
            this.texture = s;
            return this;
        }

        public boolean equals(Object other){
            if(other instanceof ShaderTexture o) {
                return shader == o.shader && texture == o.texture;
            } else {
                return false;
            }
        }
    }
    public String toString(){
        return pos + " b:" + taskRunning + " s:" + taskScheduled + " r:" + canRender;
    }

}
