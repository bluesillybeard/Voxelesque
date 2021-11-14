package game.world;

import engine.multiplatform.render.GPUChunk;
import game.GlobalBits;
import game.data.nbt.NBTElement;
import game.world.block.Block;

public class Chunk {
    private final int size;
    private final Block[][][] blocks;
    private final NBTElement[][][] nbt;
    private final GPUChunk handle;
    private final int xPos, yPos, zPos;


    public Chunk(int size, Block[][][] blocks, NBTElement[][][] nbt, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.nbt = nbt;
        this.blocks = blocks;
        this.handle = GlobalBits.render.spawnChunk(this.size, blocks, this.xPos, this.yPos, this.zPos);
    }

    /**
     * creates an empty chunk.
     */
    public Chunk(int size, int xp, int yp, int zp){
        this.xPos = xp;
        this.yPos = yp;
        this.zPos = zp;
        this.size = size;
        this.blocks = new Block[size][size][size];
        for(int x=0; x<size; x++){
            for(int y=0; y<size; y++){
                for(int z=0; z<size; z++){
                    this.blocks[x][y][z] = Block.VOID_BLOCK;
                }
            }
        }
        this.nbt = new NBTElement[size][size][size];
        this.handle = GlobalBits.render.spawnChunk(this.size, blocks, this.xPos, this.yPos, this.zPos);
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
}
