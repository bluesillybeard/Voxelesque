package game.world;

import engine.multiplatform.Render;
import engine.multiplatform.RenderUtils;
import game.GlobalBits;
import game.misc.StaticUtils;
import game.world.block.Block;
import math.FastNoiseLite;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static game.GlobalBits.renderDistance;
import static game.misc.StaticUtils.getChunkPos;
import static game.misc.StaticUtils.getChunkWorldPos;

public class World {
    private HashMap<Vector3i, Chunk> chunks;
    private final FastNoiseLite noise;
    private final ArrayList<Chunk> chunksToUnload;
    public static final int CHUNK_SIZE = 32; //MUST BE A POWER OF 2! If this is changed to a non-power of 2, many things would have to be reworked.

    public World() {

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFrequency(0.004f);
        noise.SetFractalOctaves(5);
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(0.5f);
        chunks = new HashMap<>();
        chunksToUnload = new ArrayList<>();
    }

    public void reset(){
        for(Map.Entry<Vector3i, Chunk> entry: chunks.entrySet()){
            entry.getValue().unload();
        }
        chunksToUnload.clear();
        chunks = new HashMap<>();
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
        final Render r = RenderUtils.activeRender;
        final double startTime = r.getTime();
        final float renderDistanceSquared = renderDistance * renderDistance;
        final Vector3i temp = new Vector3i();

        chunks.forEach((pos, chunk) -> {
            if (getChunkWorldPos(pos).distanceSquared(GlobalBits.playerPosition) > renderDistanceSquared) {
                chunksToUnload.add(chunk);
            }
        });

        for (Chunk chunk : chunksToUnload) {
            unloadChunk(chunk);
        }
        chunksToUnload.clear();
        boolean outOfTime = false;
        final Vector3i playerChunk = new Vector3i(getChunkPos(GlobalBits.playerPosition));
        CompletableFuture<Void> future = null;
        for(int x=playerChunk.x-(int)(renderDistance/8); x<playerChunk.x+(int)(renderDistance/8); x++){
            for(int y=playerChunk.y-(int)(renderDistance/16); y<playerChunk.y+(int)(renderDistance/16); y++){
                for(int z=playerChunk.z-(int)(renderDistance/16); z<playerChunk.z+(int)(renderDistance/16); z+=2){
                    if((r.getTime() - startTime) > targetTime) {
                        outOfTime = true;
                    }
                    //load the chunk if it's in range
                    //distanceSquared is faster - just look at the code to find out why
                    temp.set(x, y, z);
                    if(!chunks.containsKey(temp) && getChunkWorldPos(x, y, z).distanceSquared(GlobalBits.playerPosition) < renderDistanceSquared) {
                        final int finalY = y;
                        final int finalX = x;
                        final int finalZ = z;
                        future = CompletableFuture.runAsync( () -> loadChunk(finalX, finalY, finalZ));
                    }


                    temp.set(x, y, z-1);
                    if(!chunks.containsKey(temp) && getChunkWorldPos(x, y, z-1).distanceSquared(GlobalBits.playerPosition) < renderDistanceSquared) {
                        loadChunk(x, y, z-1);
                    }
                    try {
                        if (future != null)
                            future.get(); //make sure it isn't rapidly yeeting threads at the OS to get chunks built
                    } catch (Exception ignored){
                        RenderUtils.activeRender.printErrln("oh no this is bad");
                    }
                    //At an absolute minimum load 2 chunks per frame
                    if(outOfTime) break;
                }
                if(outOfTime) break;
            }
            if(outOfTime) break;
        }

        return r.getTime() - startTime;
    }

    public void unloadChunk(Chunk chunk){
        chunk.unload();
        chunks.remove(chunk.getPos());
        //todo: world saves
    }

    /**
     * loads a chunk by either loading it from the world save, or generating it if it wasn't found in the save.
     * note: uses xyz chunk coordinates
     */
    public void loadChunk(int x, int y, int z){
        Vector3i pos = new Vector3i(x, y, z);
        //todo: world saves
        Chunk chunk = generateChunk(GlobalBits.blocks, x, y, z);

        chunks.put(pos, chunk);
    }

    private Chunk generateChunk(Map<String, Block> blocks, int x, int y, int z){
        final Block[][][] blocksg = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        final Block fillBlock = blocks.get("voxelesque:grassBlock");
        final Block unfillBlock = Block.VOID_BLOCK;
        final int csx = (int) (CHUNK_SIZE * x * 0.5773502692);
        final int csy = CHUNK_SIZE * y;
        final int csz = CHUNK_SIZE * z;

        boolean empty = true;
        for(int xp = 0; xp < CHUNK_SIZE; xp++){
            for(int zp = 0; zp < CHUNK_SIZE; zp++){
                double height = noise.GetNoise(csx+(xp * 0.5773502692f), csz+zp);
                height = height*height*400;//squaring it makes it better by making lower terrain flatter, and higher terrain more varied and mountain-like
                for(int yp = 0; yp < CHUNK_SIZE; yp++){
                    if(csy+yp < height) {
                        blocksg[xp][yp][zp] = fillBlock;
                        empty = false;
                    } else {
                        blocksg[xp][yp][zp] = unfillBlock;
                    }
                }
            }
        }
        if(empty) return new Chunk(CHUNK_SIZE, x, y, z); //if it's empty, make an empty chunk.
        return new Chunk(CHUNK_SIZE, blocksg, x, y, z);
    }
}
