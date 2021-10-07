package engine.gl33.render;

import engine.gl33.model.GPUMesh;
import engine.gl33.model.GPUTexture;
import engine.multiplatform.Util.CPUMeshBuilder;
import engine.multiplatform.model.CPUMesh;

public class RenderableChunk {
    private GPUTexture texture;
    private final int size, xPos, yPos, zPos;
    private CPUMesh[][][] blocks;
    private RenderableEntity renderable;
    private boolean shouldBuild;

    private CPUMesh currentBuild;

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
    public void render(ShaderProgram shader){
        if(currentBuild != null) { //TODO: after implementing multithreading, replace this to test if it's done rendering
            renderable = new RenderableEntity(new GPUMesh(currentBuild), null, texture);
            renderable.setPosition(this.xPos * this.size / 3.465f, this.yPos * this.size / 2f, this.zPos * this.size / 2f);
        }
        if(renderable == null) return; //don't render if it can't
        renderable.setShaderProgram(shader);
        renderable.render();
    }

    /**
     * clears the vertex data from the GPU.
     */
    public void clearFromGPU(){
        renderable.getModel().mesh.cleanUp();
        //Not cleaning the texture since that is shared across all chunks, so deleting it here would cause major issues.
    }

    public boolean shouldBuildChunk(){
        return this.shouldBuild;
    }

    public void build(GPUTexture atlas){
        this.currentBuild = internalBuild();
        this.texture = atlas;
    }


    private CPUMesh internalBuild(){
        //creates a single mesh representing a chunk
        // given a 3D array of meshes.
        CPUMeshBuilder meshBuilder = new CPUMeshBuilder();

        for(int x=0; x<this.size; x++){
            for(int y=0; y<this.size; y++){
                for(int z=0; z<this.size; z++){
                    CPUMesh block = this.blocks[x][y][z];
                    meshBuilder.addBlockMeshToChunk(block, x, y, z, this.getBlockedFaces(x, y, z));
                    //wow... this code is a lot smaller than my previous attempt.
                    //To be fair, this one is a lot less flexible, and only supports using a single shader.
                    //multishadering or multitexturing requires making multiple meshes anyway, so instead that is
                    //done by calling this method several times.
                }
            }
        }
        return meshBuilder.getMesh();
    }

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
            if (i == 2) zM = (z + x & 1) * -2 + 1 + z;
            else zM = z;

            if(xM<0 || xM>this.size-1)continue;
            if(yM<0 || yM>this.size-1)continue;
            if(zM<0 || zM>this.size-1)continue;//skip it if it's outside the border (and assume it's blocked)

            CPUMesh mesh = blocks[xM][yM][zM];
            assert mesh != null; //mesh should never be null.
            if(mesh.blockedFaces == 0)continue; //skip if that mesh doesn't block faces
            blockedFaces |= (mesh.blockedFaces & (1 << i)); //add the blocked face to the bit field.
        }
        return blockedFaces;
    }
}
