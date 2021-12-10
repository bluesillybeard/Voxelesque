package game.world;

import engine.multiplatform.gpu.GPUChunk;
import game.GlobalBits;
import game.data.nbt.NBTElement;
import game.world.block.Block;
import org.joml.Vector3i;
import org.lwjgl.system.CallbackI;


public class Chunk implements Comparable<Chunk>{
    private final int size;
    private final Block[][][] blocks;
    private final NBTElement[][][] nbt;

    private final GPUChunk handle;
    private final Vector3i pos;

    public Chunk(int size, Block[][][] blocks, NBTElement[][][] nbt, int x, int y, int z){
        this.pos = new Vector3i();
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.handle = sendToRender();
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

    public void setBlock(int x, int y, int z, Block block, boolean buildImmediately){
        this.blocks[x][y][z] = block;
        GlobalBits.render.setChunkBlock(handle, block, x, y, z, buildImmediately);
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
        return GlobalBits.render.spawnChunk(this.size, this.blocks, this.pos.x, this.pos.y, this.pos.z, false);
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
}
