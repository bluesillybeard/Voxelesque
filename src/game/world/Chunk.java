package game.world;

import engine.multiplatform.RenderUtils;
import engine.multiplatform.gpu.GPUChunk;
import game.world.block.Block;
import org.joml.Vector3i;


public class Chunk implements Comparable<Chunk>{
    private final int size;
    private Block[][][] blocks;

    private GPUChunk gpuChunk;
    private final Vector3i pos;
    private boolean empty; //true if every block in this chunk is void

    public Chunk(int size, Block[][][] blocks, int x, int y, int z){
        this.pos = new Vector3i();
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        this.size = size;
        this.empty = getRealEmpty(blocks); //set empty to weather it's actually an empty chunk
        if(!empty) {
            this.blocks = blocks;//if it isn't actually empty, continue as normal
        }
        this.gpuChunk = sendToRender();
    }

    /**
     * creates an empty chunk.
     */
    public Chunk(int size, int x, int y, int z){
        this.pos = new Vector3i();
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        this.size = size;
        this.empty = true;
        this.gpuChunk = sendToRender();
    }

    public Block getBlock(int x, int y, int z){
        if(!empty)return blocks[x][y][z];
        else return Block.VOID_BLOCK;
    }

    public void setBlock(int x, int y, int z, Block block, boolean buildImmediately){
        if(empty){
            if(block == Block.VOID_BLOCK)return;
            initializeBlocks();
            blocks[x][y][z] = block;
            this.empty = false;
        } else {
            this.blocks[x][y][z] = block;
            gpuChunk.setBlock(block, x, y, z, buildImmediately);
        }
    }

    /**
     * Deletes the internal chunk handle, releasing any GPU and CPU memory associated with it.
     */
    public void unload(){
        if(gpuChunk != null) gpuChunk.delete();
        empty = true;
        blocks = null;
        gpuChunk = null;
    }

    private GPUChunk sendToRender() {
        return RenderUtils.activeRender.spawnChunk(this.size, this.blocks, this.pos.x, this.pos.y, this.pos.z, false);
    }
    @Override
    public int compareTo(Chunk o) {
        return this.hashCode() - o.hashCode();
    }

    public int HashCode(){
        return this.pos.hashCode();
    }

    public Vector3i getPos(){
        return pos;
    }

    private void initializeBlocks(){
        this.blocks = new Block[size][size][size];
        for(int xp = 0; xp<size; xp++){
            for(int yp=0; yp<size; yp++){
                for(int zp=0; zp<size; zp++){
                    this.blocks[xp][yp][zp] = Block.VOID_BLOCK;
                }
            }
        }
    }

    private boolean getRealEmpty(Block[][][] b) {
        for (int xp = 0; xp < size; xp++) {
            for (int yp = 0; yp < size; yp++) {
                for (int zp = 0; zp < size; zp++) {
                    if (b[xp][yp][zp] != Block.VOID_BLOCK) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
