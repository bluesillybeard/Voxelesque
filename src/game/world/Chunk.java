package game.world;

import engine.multiplatform.gpu.GPUChunk;
import game.GlobalBits;
import game.data.nbt.NBTElement;
import game.world.block.Block;


public class Chunk {
    private final int size;
    private final Block[][][] blocks;
    private final NBTElement[][][] nbt;

    private int numMappings;
    private final GPUChunk handle;
    private final int xPos, yPos, zPos;
    boolean empty;

    public Chunk(int size, Block[][][] blocks, NBTElement[][][] nbt, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.handle = sendToRender();
    }

    /**
     * creates an empty chunk.
     */
    public Chunk(int size, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = new Block[size][size][size];
        this.nbt = new NBTElement[size][size][size];

        for(int xp = 0; xp<size; xp++){
            for(int yp=0; yp<size; yp++){
                for(int zp=0; zp<size; zp++){
                    this.blocks[xp][yp][zp] = Block.VOID_BLOCK;
                }
            }
        }
        this.handle = sendToRender();
    }

    public Block getBlock(int x, int y, int z){
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block){
        this.blocks[x][y][z] = block;
        GlobalBits.render.setChunkBlock(handle, block, x, y, z);
    }

    public NBTElement getBlockNBT(int x, int y, int z){
        return nbt[x][y][z];
    }

    public void setBlockNBT(int x, int y, int z, NBTElement nbtElement){
        this.nbt[x][y][z] = nbtElement;
    }

    public void unload(){
        GlobalBits.render.deleteChunk(handle);
    }

    private GPUChunk sendToRender() {
        return GlobalBits.render.spawnChunk(this.size, this.blocks, this.xPos, this.yPos, this.zPos);
    }

}
