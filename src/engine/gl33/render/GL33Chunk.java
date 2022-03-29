package engine.gl33.render;

import engine.gl33.GL33Render;
import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Texture;
import engine.multiplatform.RenderUtils;
import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.gpu.GPUBlock;
import engine.multiplatform.gpu.GPUChunk;
import engine.multiplatform.model.CPUMesh;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GL33Chunk implements GPUChunk, Comparable<GL33Chunk>{
    private final Vector3i pos;
    private GPUBlock[][][] blocks;
    private final int size;
    public GL33Entity[] chunkModel;
    public boolean canRender;
    private final Vector3f cameraPos;

    private ArrayList<CPUMeshBuilder> chunkModels;
    private ArrayList<ShaderTexture> shaderTextures;

    public boolean taskRunning;
    public boolean taskScheduled;

    public GL33Chunk(int size, GPUBlock[][][] blocks, int xPos, int yPos, int zPos, Vector3f cameraPos){
        if(blocks != null && (blocks.length != size || blocks[0].length != size || blocks[0][0].length != size)){
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

    @Override
    public Vector3i getPos() {
        return pos;
    }

    @Override
    public void setData(GPUBlock[][][] blocks, boolean buildImmediately){
        setDataInternal(blocks);
        GL33Render glRender = (GL33Render)RenderUtils.activeRender;
        if(buildImmediately){
            this.taskScheduled = true;
            this.build(glRender.getChunks());
            glRender.updateAdjacentChunks(this.pos);
        } else {
            this.taskScheduled = true;
            glRender.updateChunk(this);
            glRender.updateAdjacentChunks(this.pos);
        }
    }

    private void setDataInternal(GPUBlock[][][] blocks){
        if(blocks != null && (blocks.length != this.size || blocks[0].length != this.size || blocks[0][0].length != this.size)){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + this.size + "," +
                    " but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
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

    @Override
    public GPUBlock getBlock(int x, int y, int z){
        if(blocks != null)return this.blocks[x][y][z];
        else return null;
    }

    @Override
    public int compareTo(GL33Chunk o) {
        return (int)(getChunkWorldPos(this.pos, this.size).distance(cameraPos) - getChunkWorldPos(o.pos, o.size).distance(cameraPos));
    }

    @Override
    public void setBlock(GPUBlock block, int x, int y, int z, boolean buildImmediately){
        setBlockInternal(block, x, y, z);
        GL33Render glRender = (GL33Render)RenderUtils.activeRender;
        if(buildImmediately){
            this.taskScheduled = true;
            this.build(glRender.getChunks());
            glRender.updateAdjacentChunks(this.pos);
        } else {
            this.taskScheduled = true;
            glRender.updateChunk(this);
            glRender.updateAdjacentChunks(this.pos);
        }
    }
    private void setBlockInternal(GPUBlock block, int x, int y, int z){
        if(blocks != null)blocks[x][y][z] = block;
        else blocks = new GPUBlock[size][size][size];
    }

    @Override
    public void delete(){
        GL33Render glRender = (GL33Render)RenderUtils.activeRender;
        if(!glRender.deleteChunk(this)){
            glRender.printErrln("Attempted to delete nonexistent chunk");
        }
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
                entity.model.mesh.delete();//DON'T clear the texture.
            }
        }
    }

    public void sendToGPU(){
        if (!taskRunning && !taskScheduled) {
            if(chunkModels != null && chunkModels.size() > 0){
                if(canRender)clearFromGPU();
                ArrayList<GL33Entity> model = new ArrayList<>();
                for (int i = 0; i < shaderTextures.size(); i++) {
                    CPUMesh mesh = chunkModels.get(i).getMesh();
                    if(mesh.indices.length > 0) {
                        GL33Entity entity = new GL33Entity(new GL33Mesh(mesh), shaderTextures.get(i).shader, shaderTextures.get(i).texture);
                        entity.setLocation(this.pos.x * this.size * 0.28867513459481288225f, this.pos.y * this.size * 0.5f, this.pos.z * this.size * 0.5f);
                        entity.setScale(1, 1, 1);
                        model.add(entity);
                    }
                }
                chunkModels = null;
                shaderTextures = null;
                this.chunkModel = model.toArray(new GL33Entity[0]);
                this.canRender = true;
            }
        }
    }

    /**
     *
     * @param chunks the map of chunk positions to chunk objects to get adjacent chunks from
     */
    public void build(Map<Vector3i, GPUChunk> chunks) {
        if (taskRunning) {
            RenderUtils.activeRender.printErrln("Chunk attempted to build multiple times at once:" + this);
            return;
        }
        taskRunning = true;
        if (blocks != null) {

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
                        if (block == null || block.getTexture() == null || block.getShader() == null)
                            continue; //skip rendering this block if it is null (void)
                        GL33Shader program = (GL33Shader) block.getShader();
                        GL33Texture texture = (GL33Texture) block.getTexture();
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
        }
        taskScheduled = false;
        this.taskRunning = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof GPUChunk c) {
            return this.pos.equals(c.getPos());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    private byte getBlockedFaces(int x, int y, int z, Map<Vector3i, GPUChunk> chunks){
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
            GPUChunk toUse = this;

            //[(-1, 0, 0), (0, -1, 0), (0, 0, -1), (+1, 0, 0), (0, +1, 0), (0, 0, +1)]
            if(xM<0){ //-1, 0, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x-1, pos.y, pos.z));
                else toUse = null;
                xM = size-1;
            }
            else if(xM>this.size-1){//+1, 0, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x+1, pos.y, pos.z));
                else toUse = null;

                xM = 0;
            }
            else if(yM<0) {//0, -1, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y-1, pos.z));
                else toUse = null;
                yM  = size-1;
            }
            else if(yM>this.size-1){ //0, +1, 0
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y+1, pos.z));
                else toUse = null;
                yM  = 0;
            }
            else if(zM<0) { //0, 0, -1
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y, pos.z-1));
                else toUse = null;
                zM = size-1;
            }
            else if(zM>this.size-1){ //0, 0, +1
                if(chunks != null)toUse = chunks.get(new Vector3i(pos.x, pos.y, pos.z+1));
                else toUse = null;
                zM = 0;
            }
            if(toUse == null){
                if(chunks != null)blockedFaces |= (1 << i); //if the chunk doesn't exist yet, assume it's not blocked, unless it wasn't given adjacent chunks, in which case assume it isn't blocked.
                continue;
            }
            GPUBlock block = toUse.getBlock(xM, yM, zM);
            if(block != null) {
                CPUMesh mesh = toUse.getBlock(xM, yM, zM).getMesh();

                if (mesh == null || mesh.blockedFaces == 0) continue; //skip if that mesh doesn't block faces
                blockedFaces |= (mesh.blockedFaces & (1 << i)); //add the blocked face to the bit field.
            }
        }
        return blockedFaces;
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
