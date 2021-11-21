package game.world;

import game.GlobalBits;
import game.misc.StaticUtils;
import game.world.block.Block;
import game.world.generation.PerlinNoise;
import org.joml.Vector3f;
import org.joml.Vector3i;
import Math.BetterVector3i;

import java.util.*;

import static game.GlobalBits.renderDistance;
import static game.misc.StaticUtils.getChunkPos;
import static game.misc.StaticUtils.getChunkWorldPos;

public class World {
    private static Chunk emptyChunk;
    private final Map<Vector3i, Chunk> chunks;
    private final LinkedList<Vector3i> chunksToUnload;
    private final PerlinNoise noise;
    private static final int CHUNK_SIZE = 64; //MUST BE A POWER OF 2! If this is changed to a non-power of 2, many things would have to be reworked.

    public World() {
        noise = new PerlinNoise(9, 1, 0.1, 10, 1);
        emptyChunk = new Chunk(CHUNK_SIZE, -1, -1, -1);
        chunks = new HashMap<>();
        chunksToUnload = new LinkedList<>();
    }

    public Block getBlock(int x, int y, int z){
        return getChunk(x, y, z).getBlock(x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1));
    }
    public void setBlock(int x, int y, int z, Block block){
        Chunk c = getChunk(x, y, z);
        if(c != null)c.setBlock(x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1), block);
        else System.err.println("Could not set chunk block!!!");
        //TODO: create a system that keeps track of blocks placed into nonexistent chunks
    }

    /**
     * Gets the chunk that contains the block coordinates.
     * If the chunk does not exist, it will return null
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @param z the z coordinate of the block
     * @return the Chunk that contains the block coordinates.
     */
    public Chunk getChunk(int x, int y, int z){
        return chunks.get(StaticUtils.getChunkPos(StaticUtils.getBlockWorldPos(new BetterVector3i(x, y, z))));
    }

    public void updateChunks(){
        Vector3i playerChunk = getChunkPos(GlobalBits.playerPosition);
        for(int x=playerChunk.x-(int)(renderDistance/17)-1; x<playerChunk.x+(int)(renderDistance/17)+1; x++){
            for(int y=playerChunk.y-(int)(renderDistance/31)-1; y<playerChunk.y+(int)(renderDistance/31)+1; y++){
                for(int z=playerChunk.z-(int)(renderDistance/31)-1; z<playerChunk.z+(int)(renderDistance/31)+1; z++){
                    //System.out.println(x + ", " + y + ", " + z);
                    if(!chunks.containsKey(new BetterVector3i(x, y, z)) && getChunkWorldPos(new BetterVector3i(x, y, z)).distance(GlobalBits.playerPosition) < renderDistance) {
                        loadChunk(x, y, z);
                    }
                }
            }
        }
        chunks.forEach((key, value) -> {
            if (getChunkWorldPos(key).distance(GlobalBits.playerPosition) > renderDistance) {
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
        //todo: world saves
        Chunk chunk = generateChunk(GlobalBits.blocks, x, y, z);

        chunks.put(new Vector3i(x, y, z), chunk);
    }

    private Chunk generateChunk(Map<String, Block> blocks, int x, int y, int z){
        Block[][][] blocksg = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        Block grassBlock = blocks.get("voxelesque:grassBlock");
        Block stoneBlock = Block.VOID_BLOCK;

        for(int xp = 0; xp < CHUNK_SIZE; xp++){
            for(int zp = 0; zp < CHUNK_SIZE; zp++){
                Vector3f pos = StaticUtils.getBlockWorldPos(new Vector3i(CHUNK_SIZE*x+xp, 0, CHUNK_SIZE*z+zp));
                double height = noise.getHeight(pos.x, pos.z);
                for(int yp = 0; yp < CHUNK_SIZE; yp++){
                    pos = StaticUtils.getBlockWorldPos(new Vector3i(CHUNK_SIZE*x+xp, CHUNK_SIZE * y+yp, CHUNK_SIZE*z+zp));
                    blocksg[xp][yp][zp] = stoneBlock;
                    if(pos.y < height) {
                        //don't print here, ruins world gen for unexplained reasons
                        //seriously - it's really creepy
                        blocksg[xp][yp][zp] = grassBlock;
                    }
                }
            }
        }

        return new Chunk(CHUNK_SIZE, blocksg, null, x, y, z);
    }
}
