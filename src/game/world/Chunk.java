package game.world;

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
    private final int handle;

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings, int x, int y, int z){
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
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }

    public Chunk(int size, int[][][] blocks, Map<Block, Integer> idMappings, NBTElement[][][] nbt, int x, int y, int z){
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
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }

    public Chunk(int size, int[][][] blocks, NBTElement[][][] nbt, Map<Integer, Block> blockMappings, Map<Block, Integer> idMappings, int x, int y, int z){
        this.size = size;
        this.blocks = blocks;
        this.nbt = nbt;
        this.blockMappings = blockMappings;
        this.idMappings = idMappings;
        if(blockMappings.size() != idMappings.size()) throw new IllegalStateException("blockMappings and idMappings have the different sizes!");
        this.numMappings = blockMappings.size();
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }
    public Chunk(Map<Integer, Block> blockMappings, int size, int x, int y, int z){
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.blockMappings = blockMappings;
        //generate idMappings from blockMappings
        this.idMappings = new HashMap<>();
        for(Map.Entry<Integer, Block> map: blockMappings.entrySet()){
            this.idMappings.put(map.getValue(), map.getKey());

        }
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }

    public Chunk(int size, Map<Block, Integer> idMappings, int x, int y, int z){
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.idMappings = idMappings;
        //generate blockMappings from idMappings
        this.blockMappings = new HashMap<>();
        for(Map.Entry<Block, Integer> map: idMappings.entrySet()){
            this.blockMappings.put(map.getValue(), map.getKey());
        }
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }

    public Chunk(int size, Map<Integer, Block> blockMappings, Map<Block, Integer> idMappings, int x, int y, int z){
        this.size = size;
        this.blocks = new int[size][size][size];
        this.nbt = new NBTElement[size][size][size];
        this.blockMappings = blockMappings;
        this.idMappings = idMappings;
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
    }

    /**
     * creates an empty chunk.
     */
    public Chunk(int size, int x, int y, int z){
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
        idMappings.put(Block.VOID_BLOCK, -1);
        blockMappings.put(-1, Block.VOID_BLOCK);
        ModelTextureShaderData MTSD = new ModelTextureShaderData(this);
        this.handle = GlobalBits.render.spawnChunk(size, MTSD.modelData, MTSD.textureData, MTSD.shaderData, x, y, z);
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

    private static class ModelTextureShaderData{
        public CPUMesh[][][] modelData;
        public int[][][] textureData;
        public int[][][] shaderData;

        public ModelTextureShaderData(Chunk c){
            modelData = new CPUMesh[c.size][c.size][c.size];
            textureData = new int[c.size][c.size][c.size];
            shaderData = new int[c.size][c.size][c.size];
            for(int x=0; x<c.size; x++){
                for(int y=0; y<c.size; y++){
                    for(int z=0; z<c.size; z++){
                        Block b = c.getBlock(x, y, z);
                        if(b != null) {
                            modelData[x][y][z] = b.getMesh();
                            textureData[x][y][z] = b.getTexture();
                            shaderData[x][y][z] = b.getShader();
                        } else {
                            //todo: actual empty textures and meshes
                        }
                    }
                }
            }
        }

        public ModelTextureShaderData m(CPUMesh[][][] modelData){
            this.modelData = modelData;
            return this;
        }

        public ModelTextureShaderData t(int[][][] textureData){
            this.textureData = textureData;
            return this;
        }

        public ModelTextureShaderData s(int[][][] shaderData){
            this.shaderData = shaderData;
            return this;
        }
    }
}
