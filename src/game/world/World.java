package game.world;

import game.data.vector.IntegerVector3f;
import game.world.block.Block;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final Map<IntegerVector3f, Chunk> chunks;
    private static final int CHUNK_SIZE = 64;

    public World() {
        chunks = new HashMap<>();
    }

    public Block getBlock(int x, int y, int z){
        return getChunk(x, y, z).getBlock(x%CHUNK_SIZE, y%CHUNK_SIZE, z%CHUNK_SIZE);
    }
    public void setBlock(int x, int y, int z, Block block){
        getChunk(x, y, z).setBlock(x%CHUNK_SIZE, y%CHUNK_SIZE, z%CHUNK_SIZE, block);
    }

    /**
     * Gets the chunk that contains the block coordinates, assuming a chunk is 64x64x64
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @param z the z coordinate of the block
     * @return the Chunk that contains the block coordinates.
     */
    public Chunk getChunk(int x, int y, int z){
        return chunks.get(new IntegerVector3f(x/CHUNK_SIZE, y/CHUNK_SIZE, z/CHUNK_SIZE));
    }
}
