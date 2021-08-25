package engine.render;

import engine.model.BlockMesh;
import engine.model.BlockModel;
import engine.model.Mesh;
import engine.util.BlockMeshBuilder;

import java.util.ArrayList;
import java.util.Arrays;

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
        this.size = size;
    }

    public RenderableChunk(int size, int[][][] data){
        if(data.length != size || data[0].length != size || data[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + data.length + ", " + data[0].length + ", " + data[0][0].length + ")");
        }
        this.data = data;
        this.shouldBuild = true;
        this.canRender = false;
        this.size = size;
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
                        chunkModels.get(shaderIndex).addBlockMesh(model.getMesh(), x, y, z, this.getBlockedFaces(models, x, y, z));
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


    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    private boolean[] getBlockedFaces(BlockModel[] models, int x, int y, int z){
        //exclude the blocks at the edges
        boolean[] blockedFaces = new boolean[5];
        int[] xMap = new int[]{0, 0, 0, -1, 1};
        int[] yMap = new int[]{1, -1, 0, 0, 0}; //to make the code slightly cleaner
        int[] zMap = new int[]{0, 0, ((x + z) & 1)*2-1, 0, 0}; //the ((x + z) & 1)*2-1 is to make sure Z is inverted if it needs to be.

        for(int i=0; i < 5; i++){
            //System.out.print("xyz:" + (x+xMap[i]) + "," + (y+yMap[i]) + "," + (z+zMap[i]));
            if(x+xMap[i]<0 || x+xMap[i]>this.size-1){/*System.out.println(" skipped0x" + this.size);*/continue; }
            if(y+yMap[i]<0 || y+yMap[i]>this.size-1){/*System.out.println(" skipped0y");*/continue; }
            if(z+zMap[i]<0 || z+zMap[i]>this.size-1){/*System.out.println(" skipped0z");*/continue; }//skip it if it would cause an exception

            BlockMesh mesh = models[data[x+xMap[i]][y+yMap[i]][z+zMap[i]]].getMesh(); //get the mesh of the block connecting to the face we are looking at.
            if(mesh.blockedFaces.length==0){/*System.out.println(" skipped1");*/continue;} //skip if that mesh doesn't block faces
            //System.out.println(" accepted");
            blockedFaces[i] = mesh.blockedFaces[i];
        }
        //System.out.println(Arrays.toString(blockedFaces));
        return blockedFaces;
    }
}
