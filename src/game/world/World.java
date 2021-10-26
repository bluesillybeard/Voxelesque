package game.world;

import game.GlobalBits;
import game.data.nbt.NBTFolder;
import game.world.block.Block;
import org.joml.Vector2dc;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

import static game.GlobalBits.renderDistance;
import static game.misc.StaticUtils.getChunkPos;
import static game.misc.StaticUtils.getWorldPos;

public class World {
    private final Map<Vector3i, Chunk> chunks;
    private final LinkedList<Vector3i> chunksToUnload;
    private static final int CHUNK_SIZE = 64;

    public World() {
        chunks = new HashMap<>();
        chunksToUnload = new LinkedList<>();
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
        return chunks.get(new Vector3i(x/CHUNK_SIZE, y/CHUNK_SIZE, z/CHUNK_SIZE));
    }

    public void addChunk(int x, int y, int z, Chunk chunk){
        chunks.put(new Vector3i(x, y, z), chunk);
    }

    public void removeChunk(int x, int y, int z){
        chunks.remove(new Vector3i(x, y, z));
    }



    public void updateChunks(){
        Vector3i playerChunk = getChunkPos(GlobalBits.playerPosition);
        for(int x=playerChunk.x-(int)(renderDistance/17)-1; x<playerChunk.x+(int)(renderDistance/17)+1; x++){
            for(int y=playerChunk.y-(int)(renderDistance/31)-1; y<playerChunk.y+(int)(renderDistance/31)+1; y++){
                for(int z=playerChunk.z-(int)(renderDistance/31)-1; z<playerChunk.z+(int)(renderDistance/31)+1; z++){
                    //System.out.println(x + ", " + y + ", " + z);
                    if(!chunks.containsKey(new Vector3i(x, y, z)) && getWorldPos(new Vector3i(x, y, z)).distance(GlobalBits.playerPosition) < renderDistance) {
                        loadChunk(x, y, z);
                    }
                }
            }
        }
        chunks.forEach((key, value) -> {
            if (getWorldPos(key).distance(GlobalBits.playerPosition) > renderDistance) {
                chunksToUnload.add(key);
            }
        });
        Iterator<Vector3i> chunkIterator = chunksToUnload.iterator();
        while(chunkIterator.hasNext()){
            unloadChunk(chunkIterator.next());
            chunkIterator.remove();
        }
    }

    public void unloadChunk(Vector3i chunk){
        chunks.get(chunk).unload();
        chunks.remove(chunk);
        //todo: world saves
    }

    /**
     * loads a chunk by either loading it from the world save, or generating it if it wasn't found in the save.
     * note: uses xyz chunk coordinates
     */
    public void loadChunk(int x, int y, int z){
        if(chunks.containsKey(new Vector3i(x, y, z))){
            return;
        }
        //todo: actual world generation and world saves
        Chunk chunk;
        if(y > -1){
            chunk = new Chunk(64, x, y, z);
        } else {
            chunk = randomChunk(GlobalBits.blocks, x, y, z);
        }
        chunks.put(new Vector3i(x, y, z), chunk);
    }

    private Chunk randomChunk(List<Block> blocks, int x, int y, int z){
        Chunk newChunk = new Chunk(64, x, y, z);
        Block[][][] blocksg = new Block[64][64][64];
        for(int xp = 0; xp < 64; xp++){
            for(int yp = 0; yp < 64; yp++){
                for(int zp = 0; zp < 64; zp++){
                    int random = (int) (Math.random()*blocks.size());
                    blocksg[xp][yp][zp] = blocks.get(random);
                }
            }
        }
        return new Chunk(64, );
    }
}
