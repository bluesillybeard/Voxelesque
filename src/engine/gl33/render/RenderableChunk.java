package engine.gl33.render;

import engine.gl33.model.GPUMesh;
import engine.gl33.model.GPUTexture;
import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.model.CPUMesh;
import java.util.ArrayList;

public class RenderableChunk {
    final int xPos, yPos, zPos;
    CPUMesh[][][] data;
    GPUTexture[][][] textures;
    ShaderProgram[][][] shaders;
    int size;
    RenderableEntity[] chunkModel;

    boolean shouldBuild;

    boolean canRender;

    public RenderableChunk(int size, int xPos, int yPos, int zPos){
        this.data = new CPUMesh[size][size][size];
        this.shaders = new ShaderProgram[size][size][size];
        this.shouldBuild = false;
        this.canRender = false;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public RenderableChunk(int size, CPUMesh[][][] data, int xPos, int yPos, int zPos){
        if(data.length != size || data[0].length != size || data[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + data.length + ", " + data[0].length + ", " + data[0][0].length + ")");
        }
        this.data = data;
        this.shouldBuild = true;
        this.canRender = false;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public void setData(CPUMesh[][][] data){
        if(data == null){
            this.data = null;
            return;
        }
        if(data.length != this.size || data[0].length != this.size || data[0][0].length != this.size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + this.size + "," +
                    " but the data given to the constructor has dimensions (" + data.length + ", " + data[0].length + ", " + data[0][0].length + ")");
        }
        this.data = data;
        this.shouldBuild = true;
    }
    public void setBlock(CPUMesh block, int x, int y, int z){
        data[x][y][z] = block;
    }
    public void render(){
        if(!canRender) return; //don't render if it can't
        for(RenderableEntity entity: chunkModel){
            entity.render(); //the entities positions are already set to the right place in the build method
        }
    }

    /**
     * clears the vertex data from the GPU.
     */
    public void clearFromGPU(){
        for(RenderableEntity entity: this.chunkModel){
            entity.getModel().mesh.cleanUp();//DON'T clear the texture.
        }
    }

    /**
     *
     * @return true if it finished, false if it paused
     */
    public boolean build() {
        //todo: multithreading
        if (this.shouldBuild) {
            if (canRender) clearFromGPU(); //clear the previous model to avoid memory leaks.
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
            ArrayList<CPUMeshBuilder> chunkModels = new ArrayList<>();
            ArrayList<ShaderTexture> shaderTextures = new ArrayList<>();
            ShaderTexture TSP = new ShaderTexture();

            for (int x = 0; x < data.length; x++) {
                for (int y = 0; y < data[x].length; y++) {
                    for (int z = 0; z < data[x][y].length; z++) {
                        CPUMesh block = data[x][y][z];
                        if (block == null) continue; //skip rendering this block if it is null (void)
                        ShaderProgram program = shaders[x][y][z];
                        GPUTexture texture = textures[x][y][z];
                        int shaderTextureIndex = shaderTextures.indexOf(TSP.s(program).t(texture));
                        if (shaderTextureIndex == -1) {
                            shaderTextureIndex = chunkModels.size();
                            shaderTextures.add(new ShaderTexture(program, texture));
                            chunkModels.add(new CPUMeshBuilder(this.size*this.size*this.size*10));//todo: test optimal factor (currently 10)
                        }
                        //cloning, index removal, and vertex position modification done within the BlockMeshBuilder
                        chunkModels.get(shaderTextureIndex).addBlockMeshToChunk(block, x, y, z, this.getBlockedFaces(x, y, z));
                    }
                }
            }
            //finally, take the buffers and convert them into a renderable form.
            RenderableEntity[] model = new RenderableEntity[shaderTextures.size()];
            for (int i = 0; i < model.length; i++) {
                RenderableEntity entity = new RenderableEntity(new GPUMesh(chunkModels.get(i).getMesh()), shaderTextures.get(i).shader, shaderTextures.get(i).texture);
                entity.setPosition(this.xPos*this.size*0.288675134595f, this.yPos*this.size*0.5f, this.zPos*this.size*0.5f);
                entity.setScale(1, 1, 1);
                model[i] = entity;
            }
            this.chunkModel = model;
            this.canRender = true;
            this.shouldBuild = false;
        }
        return true;
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

            CPUMesh mesh = this.data[xM][yM][zM];
            assert mesh != null; //mesh should never be null.
            if(mesh.blockedFaces == 0)continue; //skip if that mesh doesn't block faces
            blockedFaces |= (mesh.blockedFaces & (1 << i)); //add the blocked face to the bit field.
        }
        return blockedFaces;
    }


    private static class ShaderTexture{
        public ShaderProgram shader;
        public GPUTexture texture;

        public ShaderTexture(){

        }
        public ShaderTexture(ShaderProgram s, GPUTexture t){
            this.shader = s;
            this.texture = t;
        }
        public ShaderTexture s(ShaderProgram s){
            this.shader = s;
            return this;
        }
        public ShaderTexture t(GPUTexture s){
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
}
