package engine.gl33.render;

import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.Util.ExecutionThread;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;

import java.util.ArrayList;

public class RenderableChunk {
    private int size, xPos, yPos, zPos;
    private CPUMesh[][][] blocks;
    private RenderableEntity renderable;
    private boolean shouldBuild;

    private CPUMesh currentBuild;
    private ExecutionThread currentBuilder;

    public RenderableChunk(int size, int xPos, int yPos, int zPos){
        this.blocks = new CPUMesh[size][size][size];
        this.shouldBuild = false;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public RenderableChunk(int size, CPUMesh[][][] blocks, int xPos, int yPos, int zPos){
        if(blocks.length != size || blocks[0].length != size || blocks[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
        this.shouldBuild = true;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }


    public void setData(CPUMesh[][][] blocks){
        if(blocks == null){
            this.blocks = null;
            return;
        }
        if(blocks.length != this.size || blocks[0].length != this.size || blocks[0][0].length != this.size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + this.size + "," +
                    " but the data given to the constructor has dimensions (" + blocks.length + ", " + blocks[0].length + ", " + blocks[0][0].length + ")");
        }
        this.blocks = blocks;
        this.shouldBuild = true;
    }
    public void setBlock(CPUMesh block, int x, int y, int z){
        blocks[x][y][z] = block;
    }
    public void render(){
        if(renderable == null) return; //don't render if it can't
        renderable.setPosition(xPos*size, yPos*size, zPos*size);
        renderable.render();

    }

    /**
     * clears the vertex data from the GPU.
     */
    public void clearFromGPU(){
        renderable.getModel().mesh.cleanUp();
        //Not cleaning the texture since that is shared across all chunks, so deleting it here would cause major issues.
    }

    public void build(ExecutionThread thread){

    }

    private CPUMesh internalBuild(CPUMesh[][][] blocks){

        CPUMeshBuilder meshBuilders = new CPUMeshBuilder();

        for(int x=0; x<size; x++){
            for(int y=0; y<size; y++){
                for(int z=0; z<size; z++){

                    //YOU were going to write this method!
                }
            }
        }
        return meshBuilders.getMesh();
    }
}
