package engine.gl33.render;

import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Texture;
import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.RenderBlockModel;
import engine.multiplatform.render.GPUChunk;

import java.util.ArrayList;

public class GL33Chunk implements GPUChunk {
    private final int xPos, yPos, zPos;
    RenderBlockModel[][][] blocks;
    private final int size;
    private GL33Entity[] chunkModel;
    private boolean canRender;

    private final ArrayList<CPUMeshBuilder> chunkModels = new ArrayList<>();
    private final ArrayList<ShaderTexture> shaderTextures = new ArrayList<>();

    public boolean taskRunning;

    public GL33Chunk(int size, RenderBlockModel[][][] blocks, int xPos, int yPos, int zPos){
        if(blocks.length != size || blocks[0].length != size || blocks[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
        this.canRender = false;
        taskRunning = false;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public void setData(RenderBlockModel[][][] blocks){
        if(blocks != null && (blocks.length != this.size || blocks[0].length != this.size || blocks[0][0].length != this.size)){
            throw new IllegalStateException("a chunk's blocks cannot be any other size than " + this.size + "," +
                    " but the blocks given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");

        }
        this.blocks = blocks;
    }
    public void setBlock(RenderBlockModel block, int x, int y, int z){
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
        if(taskRunning){
            return false;
        } else if(chunkModels.size() > 0){
            if(canRender)clearFromGPU();
            GL33Entity[] model = new GL33Entity[shaderTextures.size()];
            for (int i = 0; i < model.length; i++) {
                GL33Entity entity = new GL33Entity(new GL33Mesh(chunkModels.get(i).getMesh()), shaderTextures.get(i).shader, shaderTextures.get(i).texture);
                entity.setPosition(this.xPos * this.size * 0.288675134595f, this.yPos * this.size * 0.5f, this.zPos * this.size * 0.5f);
                entity.setScale(1, 1, 1);
                model[i] = entity;
            }
            chunkModels.clear();
            shaderTextures.clear();
            //chunkModels = new ArrayList<>();
            //shaderTextures = new ArrayList<>();
            this.chunkModel = model;
            this.canRender = true;
            return true;
        }
        return false;
    }

    public void build() {
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
        ShaderTexture TSP = new ShaderTexture();
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                for (int z = 0; z < blocks[x][y].length; z++) {
                    RenderBlockModel block = blocks[x][y][z];
                    if (block == null) continue; //skip rendering this block if it is null (void)
                    int shaderTextureIndex = shaderTextures.indexOf(TSP.s((GL33Shader) block.getShader()).t((GL33Texture) block.getTexture()));
                    if (shaderTextureIndex == -1) {
                        shaderTextureIndex = chunkModels.size();
                        shaderTextures.add(new ShaderTexture((GL33Shader) block.getShader(), (GL33Texture) block.getTexture()));
                        chunkModels.add(new CPUMeshBuilder(this.size * this.size * this.size * 10, true));//todo: test optimal factor (currently 10)
                    }
                    //cloning, index removal, and vertex position modification done within the BlockMeshBuilder
                    chunkModels.get(shaderTextureIndex).addBlockMeshToChunk(block.getMesh(), x, y, z, /*this.getBlockedFaces(x, y, z)*/(byte) 0);
                    if (Thread.interrupted()) {
                        return;
                    }
                }
            }
        }
        this.taskRunning = false;
    }


    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    private byte getBlockedFaces(int x, int y, int z){
        //exclude the blocks at the edges
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
                zM = (z + x & 1) * -2 + 1 + z;
            } else {
                zM = z;
            }

            if(xM<0 || xM>this.size-1)continue;
            if(yM<0 || yM>this.size-1)continue;
            if(zM<0 || zM>this.size-1)continue;//skip it if it's outside the border (and assume it's blocked)

            CPUMesh mesh = this.blocks[xM][yM][zM].getMesh();
            assert mesh != null; //mesh should never be null.
            if(mesh.blockedFaces == 0)continue; //skip if that mesh doesn't block faces
            blockedFaces |= (mesh.blockedFaces & (1 << i)); //add the blocked face to the bit field.
        }
        return blockedFaces;
    }

    /**
     * 0: unknown
     * 1: GL33
     * 2: GL21
     * 3: DX9
     *
     * @return the integer ID of the render class this mesh belongs to.
     */
    @Override
    public int getRenderClass() {
        return 1;
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
        return "chunk:(" + xPos + "," + yPos + "," + zPos + ") s:" + taskRunning + " r:" + canRender;
    }
}
