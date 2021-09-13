package game.world;

import game.data.nbt.NBTFolder;
import game.data.vector.IntegerVector3f;
import game.misc.StaticUtils;
import game.world.block.Block;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    public void addChunk(int x, int y, int z, Chunk chunk){
        chunks.put(new IntegerVector3f(x, y, z), chunk);
    }

    public void removeChunk(int x, int y, int z){
        chunks.remove(new IntegerVector3f(x, y, z));
    }

    public void unloadChunks(Vector3f pos, float distance){
        chunks.entrySet().removeIf(entry -> StaticUtils.getDistance(pos, new Vector3f(entry.getKey().x, entry.getKey().y, entry.getKey().z)) > distance);
        //remove all the chunks that are further from the point than specified in the distance.
    }

    public void updateChunks(){

    }


    /**
     * loads a chunk by either loading it from the world save, or generating it if it wasn't found in the save.
     * note: uses xyz chunk coordinates
     */
    public void loadChunk(int x, int y, int z){
        //todo: actual world generation and world saves
        Chunk chunk;
        if(y > 0){
            chunk = new Chunk(64);
        } else {
            chunk = randomChunk(null);
        }
    }

    private Chunk randomChunk(List<Block> blocks){
        Chunk newChunk = new Chunk(64);
        for(int x = 0; x < 64; x++){
            for(int y = 0; y < 64; y++){
                for(int z = 0; z < 64; z++){
                    int random = (int) (Math.random()*blocks.size());
                    newChunk.setBlock(x, y, z, blocks.get(random));
                    newChunk.setBlockNBT(x, y, z, new NBTFolder("block"));
                }
            }
        }
        return newChunk;
    }
}
