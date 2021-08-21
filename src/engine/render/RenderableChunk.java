package engine.render;

import engine.model.BlockMesh;
import engine.model.BlockModel;
import engine.model.Model;
import engine.util.BlockMeshBuilder;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

public class RenderableChunk {
    int[][][] data;
    int size;
    RenderableEntity[] chunkModel;

    boolean shouldBuild;

    boolean canRender;

    public RenderableChunk(int size){
        this.data = new int[size][size][size];
        this.shouldBuild = false;
        this.canRender = false;
    }

    public RenderableChunk(int size, int[][][] data){
        if(data.length != size || data[0].length != size || data[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + data.length + ", " + data[0].length + ", " + data[0][0].length + ")");
        }
        this.data = data;
        this.shouldBuild = true;
        this.canRender = false;
    }

    public void setData(int[][][] data){
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
    public void setBlock(int block, int x, int y, int z){
        data[x][y][x] = block;
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
        for(RenderableEntity model: this.chunkModel){
            model.getModel().cleanUp();
        }
    }
    public void build(BlockModel[] models){
        if(this.shouldBuild) {
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
            ArrayList<BlockMeshBuilder> chunkModels = new ArrayList<>();
            ArrayList<ShaderProgram> shaderPrograms = new ArrayList<>();

            for (int x = 0; x < data.length; x++) {
                for (int y = 0; y < data[x].length; y++) {
                    for (int z = 0; z < data[x][y].length; z++) {
                        int block = data[x][y][z];
                        if (block == -1) continue; //skip rendering this block if it is -1.
                        BlockModel model = models[block];
                        ShaderProgram program = model.getShader();
                        int shaderIndex = shaderPrograms.indexOf(program);
                        if (shaderIndex == -1) {
                            shaderPrograms.add(program);
                            chunkModels.add(new BlockMeshBuilder());
                            shaderIndex = chunkModels.size() - 1;
                        }
                        //cloning, index removal, and vertex position modification done within the BlockMeshBuilder
                        chunkModels.get(shaderIndex).addBlockMesh(model.getMesh(), x, y, z);
                    }
                }
            }
            //finally, take the buffers and convert them into a renderable form.
            RenderableEntity[] model = new RenderableEntity[shaderPrograms.size()];
            for (int i = 0; i < model.length; i++) {
                model[i] = new RenderableEntity(chunkModels.get(i).getMesh(), shaderPrograms.get(i), models[0].getTexture());
            }
            this.chunkModel = model;
            this.canRender = true;
            this.shouldBuild = false;
        }
    }
}
