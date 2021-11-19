package game.world;

import engine.multiplatform.gpu.GPUShader;
import engine.multiplatform.model.CPUMesh;
import game.GlobalBits;
import game.data.nbt.NBTElement;
import game.world.block.Block;

import java.util.HashMap;
import java.util.Map;

public class Chunk {
    private final int size;
    private final int[][][] blocks;
    private final NBTElement[][][] nbt;
    private final Map<Integer, Block> blockMappings;
    private final Map<Block, Integer> idMappings;

    private int numMappings;
    private final int handle, xPos, yPos, zPos;

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.blockMappings = blockMappings;
        //generate idMappings from blockMappings
        this.idMappings = new HashMap<>();
        for(Map.Entry<Integer, Block> map: blockMappings.entrySet()){
            this.idMappings.put(map.getValue(), map.getKey());
            numMappings++;
        }
        this.handle = sendToRender();
    }
    public Chunk(int size, Block[][][] blocks, NBTElement[][][] nbt, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.nbt = nbt;
        this.blocks = new int[size][size][size];
        blockMappings = new HashMap<>();
        idMappings = new HashMap<>();
        for(int xp=0; xp<size; xp++){
            for(int yp=0; yp<size; yp++){
                for(int zp=0; zp<size; zp++){
                    Block block = blocks[xp][yp][zp];
                    if(!blockMappings.containsValue(block)){
                        blockMappings.put(numMappings, block);
                        idMappings.put(block, numMappings++);
                    }
                    this.blocks[xp][yp][zp] = idMappings.get(block);
                }
            }
        }
        this.handle = sendToRender();
    }

    public Chunk(int size, int[][][] blocks, Map<Block, Integer> idMappings, NBTElement[][][] nbt, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.idMappings = idMappings;
        //generate blockMappings from idMappings
        this.blockMappings = new HashMap<>();
        for(Map.Entry<Block, Integer> map: idMappings.entrySet()){
            this.blockMappings.put(map.getValue(), map.getKey());
            numMappings++;
        }
        this.handle = sendToRender();
    }

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings, Map<Block, Integer> idMappings, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.blockMappings = blockMappings;
        this.idMappings = idMappings;
        if(blockMappings.size() != idMappings.size()) throw new IllegalStateException("blockMappings and idMappings have the different sizes!");
        this.numMappings = blockMappings.size();
        this.handle = sendToRender();
    }
    public Chunk(Map<Integer, Block> blockMappings, int size, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.blockMappings = blockMappings;
        //generate idMappings from blockMappings
        this.idMappings = new HashMap<>();
        for(Map.Entry<Integer, Block> map: blockMappings.entrySet()){
            this.idMappings.put(map.getValue(), map.getKey());

        }
        this.handle = sendToRender();
    }

    public Chunk(int size, Map<Block, Integer> idMappings, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.idMappings = idMappings;
        //generate blockMappings from idMappings
        this.blockMappings = new HashMap<>();
        for(Map.Entry<Block, Integer> map: idMappings.entrySet()){
            this.blockMappings.put(map.getValue(), map.getKey());
        }
        this.handle = sendToRender();
    }

    public Chunk(int size, Map<Integer, Block> blockMappings, Map<Block, Integer> idMappings, int x, int y, int z){
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.blockMappings = blockMappings;
        this.idMappings = idMappings;
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
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];

        for(int xp = 0; xp<size; xp++){
            for(int yp=0; yp<size; yp++){
                for(int zp=0; zp<size; zp++){
                    this.blocks[xp][yp][zp] = -1;
                }
            }
        }

        this.idMappings = new HashMap<>();
        this.blockMappings = new HashMap<>();
        this.idMappings.put(Block.VOID_BLOCK, -1);
        this.blockMappings.put(-1, Block.VOID_BLOCK);
        this.handle = sendToRender();
    }

    public Block getBlock(int x, int y, int z){
        return blockMappings.get(blocks[x][y][z]);
    }

    public void setBlock(int x, int y, int z, Block block){
        Integer intBlock = idMappings.get(block);
        if(intBlock == null) {
            intBlock = numMappings;
            idMappings.put(block, numMappings++);
        }
        this.blocks[x][y][z] = intBlock;
        GlobalBits.render.setChunkBlock(handle, block.getMesh(), block.getTexture(), block.getShader(), x, y, z);
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

    private int sendToRender() {
        CPUMesh[][][] modelData = new CPUMesh[this.size][this.size][this.size];
        int[][][] textureData = new int[this.size][this.size][this.size];
        GPUShader[][][] shaderData = new GPUShader[this.size][this.size][this.size];
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                for (int z = 0; z < this.size; z++) {
                    Block b = this.getBlock(x, y, z);
                    if(b == null) b = Block.VOID_BLOCK;

                    modelData[x][y][z] = b.getMesh();
                    textureData[x][y][z] = b.getTexture();
                    shaderData[x][y][z] = b.getShader();
                }
            }
        }
        return GlobalBits.render.spawnChunk(this.size, modelData, textureData, shaderData, this.xPos, this.yPos, this.zPos);
    }
}
