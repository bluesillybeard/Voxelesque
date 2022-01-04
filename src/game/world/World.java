package game.world;

import engine.multiplatform.Render;
import engine.multiplatform.RenderUtils;
import game.GlobalBits;
import game.misc.HashComparator;
import game.misc.StaticUtils;
import game.world.block.Block;
import math.FastNoiseLite;
import org.joml.Vector3f;
import org.joml.Vector3i;
import math.BetterVector3i;

import java.util.*;

import static game.GlobalBits.renderDistance;
import static game.misc.StaticUtils.getChunkPos;
import static game.misc.StaticUtils.getChunkWorldPos;

public class World {
    private Map<Vector3i, Chunk> chunks;
    private LinkedList<Vector3i> chunksToUnload;
    private final FastNoiseLite noise;
    public static final int CHUNK_SIZE = 32; //MUST BE A POWER OF 2! If this is changed to a non-power of 2, many things would have to be reworked.

    private static final Vector3i temp = new Vector3i();

    public World() {

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFrequency(0.004f);
        noise.SetFractalOctaves(5);
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(0.5f);
        chunks = new TreeMap<>(new HashComparator());
        chunksToUnload = new LinkedList<>();
    }

    public void reset(){
        for(Map.Entry<Vector3i, Chunk> entry: chunks.entrySet()){
            entry.getValue().unload();
        }
        chunksToUnload = new LinkedList<>();
        chunks = new TreeMap<>(new HashComparator());
    }

    public Map<Vector3i, Chunk> getChunks(){
        return chunks;
    }

    public Block getBlock(int x, int y, int z){
        Chunk c = getBlockChunk(x, y, z);
        if(c != null)
            return c.getBlock(x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1));
        else return null;
    }

    public Block getBlock(Vector3i pos){
        return getBlock(pos.x, pos.y, pos.z);
    }

    public void setBlock(int x, int y, int z, Block block, boolean buildImmediately){
        Chunk c = getBlockChunk(x, y, z);
        if(c != null)c.setBlock(x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1), block, buildImmediately);
        else System.err.println("Could not set chunk block!!!");
        //TODO: create a system that keeps track of blocks placed into nonexistent chunks
    }

    public void setBlock(Vector3i pos, Block block, boolean buildImmediately){
        setBlock(pos.x, pos.y, pos.z, block, buildImmediately);
    }

    /**
     * Gets the chunk that contains the block coordinates.
     * If the chunk does not exist, it will return null
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @param z the z coordinate of the block
     * @return the Chunk that contains the block coordinates.
     */
    public Chunk getBlockChunk(int x, int y, int z){
        Vector3i pos = new Vector3i((x & -CHUNK_SIZE)/CHUNK_SIZE, (y & -CHUNK_SIZE)/CHUNK_SIZE, (z & -CHUNK_SIZE)/CHUNK_SIZE);
        //the '& -CHUNK_SIZE' is required because of a strange issue with integer division and negative numbers.
        //It is also part of why CHUNK_SIZE must ALWAYS ALWAYS ALWAYS be a power of 2. If it isn't, weird stuff will happen.
        return chunks.get(pos);
    }


    public double updateChunks(double targetTime){
        Render r = RenderUtils.activeRender;
        double startTime = r.getTime();


        Vector3i playerChunk = getChunkPos(GlobalBits.playerPosition);
        for(int x=playerChunk.x-(int)(renderDistance/6.9); x<playerChunk.x+(int)(renderDistance/6.9); x++){ //haha 69 lol (what a coincidence)
            for(int y=playerChunk.y-(int)(renderDistance/12); y<playerChunk.y+(int)(renderDistance/12); y++){
                for(int z=playerChunk.z-(int)(renderDistance/12); z<playerChunk.z+(int)(renderDistance/12); z++){
                    //System.out.println(x + ", " + y + ", " + z);
                    if(!chunks.containsKey(temp.set(x, y, z)) && getChunkWorldPos(temp).distance(GlobalBits.playerPosition) < renderDistance) {
                        loadChunk(x, y, z);
                    }
                    if((r.getTime() - startTime) > targetTime)
                        break;
                }
                if((r.getTime() - startTime) > targetTime)
                    break;
            }
            if((r.getTime() - startTime) > targetTime)
                break;
        }
        for (Vector3i pos : chunks.keySet()) {
            if (getChunkWorldPos(pos).distance(GlobalBits.playerPosition) > renderDistance) {
                chunksToUnload.add(pos);
            }
        }
        Iterator<Vector3i> chunkIterator = chunksToUnload.iterator();
        while(chunkIterator.hasNext()){
            unloadChunk(chunkIterator.next());
            chunkIterator.remove();
        }
        return r.getTime() - startTime;
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
        if(chunks.containsKey(new BetterVector3i(x, y, z))){
            return;
        }
        //todo: world saves
        Chunk chunk = generateChunk(GlobalBits.blocks, x, y, z);

        chunks.put(new BetterVector3i(x, y, z), chunk);
    }

    private Chunk generateChunk(Map<String, Block> blocks, int x, int y, int z){
        Block[][][] blocksg = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        Block grassBlock = blocks.get("voxelesque:grassBlock");
        Block stoneBlock = Block.VOID_BLOCK;

        for(int xp = 0; xp < CHUNK_SIZE; xp++){
            for(int zp = 0; zp < CHUNK_SIZE; zp++){
                Vector3f pos = StaticUtils.getBlockWorldPos(temp.set(CHUNK_SIZE*x+xp, 0, CHUNK_SIZE*z+zp));
                double height = noise.GetNoise(pos.x, pos.z)*20*noise.GetNoise(pos.x, pos.z)*20; //squaring it makes it better by making lower terrain flatter, and higher terrain more varied and mountain-like
                for(int yp = 0; yp < CHUNK_SIZE; yp++){
                    pos = StaticUtils.getBlockWorldPos(temp.set(CHUNK_SIZE*x+xp, CHUNK_SIZE * y+yp, CHUNK_SIZE*z+zp));
                    blocksg[xp][yp][zp] = stoneBlock;
                    if(pos.y < height) {
                        //don't print here, ruins world gen for unexplained reasons
                        //seriously - it's really creepy
                        blocksg[xp][yp][zp] = grassBlock;
                    }
                }
            }
        }

        return new Chunk(CHUNK_SIZE, blocksg, x, y, z);
    }
}
