package game.world;

import game.GlobalBits;
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

    private boolean chunkShouldUnload(int cx, int cy, int cz, int px, int py, int pz){
        return false; //todo: do something about this
    }

    public void updateChunks(){
        //chunks.entrySet().removeIf(entry ->
        //        chunkShouldUnload(entry.getKey().x, entry.getKey().y, entry.getKey().z,
        //                (int)GlobalBits.playerPosition.x, (int)GlobalBits.playerPosition.y, (int)GlobalBits.playerPosition.z));
        for(int x=0; x<GlobalBits.renderDistance; x++){
            for(int y=0;y<GlobalBits.renderDistance/2;y++){
                for(int z=0;z<GlobalBits.renderDistance/2;z++){
                    updateChunk(x, y, z);
                }
            }
        }

    }

    private void updateChunk(int x, int y, int z){
        if(!chunks.containsKey(new IntegerVector3f(x, y, z))){
            loadChunk(x, y, z);
        }
    }


    /**
     * loads a chunk by either loading it from the world save, or generating it if it wasn't found in the save.
     * note: uses xyz chunk coordinates
     */
    public void loadChunk(int x, int y, int z){
        if(chunks.containsKey(new IntegerVector3f(x, y, z))){
            return;
        }
        //todo: actual world generation and world saves
        Chunk chunk;
        if(y > 0){
            chunk = new Chunk(64, x, y, z);
        } else {
            chunk = randomChunk(GlobalBits.blocks, x, y, z);
        }
        chunks.put(new IntegerVector3f(x, y, z), chunk);
    }

    private Chunk randomChunk(List<Block> blocks, int x, int y, int z){
        Chunk newChunk = new Chunk(64, x, y, z);
        for(int xp = 0; xp < 64; xp++){
            for(int yp = 0; yp < 64; yp++){
                for(int zp = 0; zp < 64; zp++){
                    int random = (int) (Math.random()*blocks.size());
                    newChunk.setBlock(xp, yp, zp, blocks.get(random));
                    newChunk.setBlockNBT(xp, yp, zp, new NBTFolder("block"));
                }
            }
        }
        return newChunk;
    }
}
