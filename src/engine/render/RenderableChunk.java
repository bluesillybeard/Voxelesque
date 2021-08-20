package engine.render;

import engine.model.Model;

import java.util.ArrayList;

public class RenderableChunk {
    int[][][] data;
    int size;
    Model[] chunkModel;
    ShaderProgram[] chunkShaders;

    boolean shouldBuild;

    public RenderableChunk(int size){
        this.data = new int[size][size][size];
        this.shouldBuild = false;
    }

    public RenderableChunk(int size, int[][][] data){
        if(data.length != size || data[0].length != size || data[0][0].length != size){
            throw new IllegalStateException("a chunk's data cannot be any other size than " + size + "," +
                    "\n but the data given to the constructor has dimensions (" + data.length + ", " + data[0].length + ", " + data[0][0].length + ")");
        }
        this.data = data;
        this.shouldBuild = true;
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
    }
}
