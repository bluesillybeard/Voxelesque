package game.world;

import game.data.nbt.NBTElement;
import game.world.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class Chunk {
    private final int size;
    private final int[][][] blocks;
    private final NBTElement[][][] nbt;
    private final Map<Integer, Block> blockMappings;
    private final Map<Block, Integer> idMappings;

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings){
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.blockMappings = blockMappings;
        //generate idMappings from blockMappings
        this.idMappings = new HashMap<>();
        for(Map.Entry<Integer, Block> map: blockMappings.entrySet()){
            this.idMappings.put(map.getValue(), map.getKey());
        }
    }

    public Chunk(int size, int[][][] blocks, Map<Block, Integer> idMappings, NBTElement[][][] nbt){
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.idMappings = idMappings;
        //generate blockMappings from idMappings
        this.blockMappings = new HashMap<>();
        for(Map.Entry<Block, Integer> map: idMappings.entrySet()){
            this.blockMappings.put(map.getValue(), map.getKey());
        }
    }

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings, Map<Block, Integer> idMappings){
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.blockMappings = blockMappings;
        this.idMappings = idMappings;
    }

    /**
     * creates an empty chunk.
     */
    public Chunk(int size){
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];

        for(int x = 0; x<size; x++){
            for(int y=0; y<size; y++){
                for(int z=0; z<size; z++){
                    this.blocks[z][y][x] = -1;
                }
            }
        }

        this.idMappings = new HashMap<>();
        this.blockMappings = new HashMap<>();
        idMappings.put(Block.VOID_BLOCK, -1);
        blockMappings.put(-1, Block.VOID_BLOCK);
    }

    public Block getBlock(int x, int y, int z){
        return blockMappings.get(blocks[x][y][z]);
    }

    public void setBlock(int x, int y, int z, Block block){
        this.blocks[x][y][z] = idMappings.get(block);
    }

    public NBTElement getBlockNBT(int x, int y, int z){
        return nbt[x][y][z];
    }

    public void setBlockNBT(int x, int y, int z, NBTElement nbtElement){
        this.nbt[x][y][z] = nbtElement;
    }

}
