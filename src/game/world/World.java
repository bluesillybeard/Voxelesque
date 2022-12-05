package game.world;

import engine.multiplatform.Render;
import engine.multiplatform.RenderUtils;
import engine.multiplatform.gpu.GPUChunk;
import game.GlobalBits;
import game.world.block.Block;
import org.joml.Vector3f;
import util.noise.FastNoiseLite;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static game.GlobalBits.*;

public class World {

    int chunkIndex;
    private final FastNoiseLite noise;
    private final ArrayList<GPUChunk> chunksToUnload;
    private final Set<Vector3i> scheduledChunks = Collections.synchronizedSet(new HashSet<>());


    //private boolean pausedBatch;
    //private int batchX;
    //private int batchY;
    //private int batchZ;
    //private final PriorityThreadPoolExecutor<DistanceRunnable3i> executor = new PriorityThreadPoolExecutor<>(DistanceRunnable3i.inOrder, Runtime.getRuntime().availableProcessors());
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private CompletableFuture<Void> future;
    public static final int CHUNK_SIZE = 32; //MUST BE A POWER OF 2! If this is changed to a non-power of 2, many things would have to be reworked.

    public World() {

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFrequency(0.004f);
        noise.SetFractalOctaves(5);
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(0.5f);
        chunksToUnload = new ArrayList<>();
    }

    public void reset(){
        chunksToUnload.addAll(RenderUtils.activeRender.getChunks().values());
    }

    public Block getBlock(int x, int y, int z){
        GPUChunk c = getBlockChunk(x, y, z);
        if(c != null)
            return (Block)c.getBlock(x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1));
        else return null;
    }

    public Block getBlock(Vector3i pos){
        return getBlock(pos.x, pos.y, pos.z);
    }

    public void setBlock(int x, int y, int z, Block block, boolean buildImmediately){
        GPUChunk c = getBlockChunk(x, y, z);
        if(c != null)c.setBlock(block, x&(CHUNK_SIZE-1), y&(CHUNK_SIZE-1), z&(CHUNK_SIZE-1), buildImmediately);
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
    public GPUChunk getBlockChunk(int x, int y, int z){
        Vector3i pos = new Vector3i((x & -CHUNK_SIZE)/CHUNK_SIZE, (y & -CHUNK_SIZE)/CHUNK_SIZE, (z & -CHUNK_SIZE)/CHUNK_SIZE);
        //the '& -CHUNK_SIZE' is required because of a strange issue with integer division and negative numbers.
        //It is also part of why CHUNK_SIZE must ALWAYS ALWAYS ALWAYS be a power of 2. If it isn't, weird stuff will happen.
        return render.getChunk(pos);
    }

    public double updateChunks(){
        final Render r = RenderUtils.activeRender;
        final double startTime = r.getTime();
        final float renderDistanceSquared = renderDistance * renderDistance;
        render.getChunks().forEach((pos, chunk) -> {
            if (RenderUtils.getChunkWorldPos(pos).distanceSquared(GlobalBits.playerPosition) > renderDistanceSquared) {
                chunksToUnload.add(chunk);
            }
        });

        unloadChunks(chunksToUnload);

        chunksToUnload.clear();
        //We do this asynchronously since it takes >200ms since many threads are fighting for access to scheduledChunks
        if(future != null){
            if(future.isDone()) future = null;
        }
        if(future == null)
            future = CompletableFuture.runAsync(() -> {
                //Hi. It's me from the far future. It's been quite a while since I did any substantial change in this codebase.
                // I decided to optimize this a bit because why not. I'm bored and I want to see if it's even possible to fix this mess.
                // this is a load of crap. It's very easy to see that I really didn't know what I was doing.
                // In fact, I really shot myself in the foot here, because the way I wrote my code made it hard to optimize code that calls it.
                //It's not hard to see why I abandoned this project.

                //First, get a range of chunks, in chunk space, by converting the render distance into the equivalent ranges in chunk space.
                // Since each of the 3 dimensions might have a different scale factor, we use this function to do that annoying math for us.
                //also, this is just bad. two new statements in a single line of code? It's like I was trying to make my code unoptimized!
                // I used temporary variables to avoid allocating memory, but past me didn't think to take a mutable object in lol.
                Vector3i chunkRange = new Vector3i(RenderUtils.getChunkPos(new Vector3f(renderDistance, renderDistance, renderDistance)));
                for (int x = -chunkRange.x; x < chunkRange.x; x++) {
                    for (int y = -chunkRange.x; y < chunkRange.y; y++) {
                        for (int z = -chunkRange.x; z < chunkRange.z; z++) {
                            Vector3i chunkPos = new Vector3i();
                            playerChunk.add(x, y, z, chunkPos);
                            if (!render.hasChunk(chunkPos) && RenderUtils.getChunkWorldPos(chunkPos).distanceSquared(GlobalBits.playerPosition) < renderDistanceSquared && !scheduledChunks.contains(chunkPos)) {
                                scheduledChunks.add(chunkPos);
                                executor.submit(() -> {
                                    loadChunk(chunkPos.x, chunkPos.y, chunkPos.z);
                                    scheduledChunks.remove(chunkPos);
                                });
                            }
                        }
                    }
                }

                // I can't believe I thought that this would be a good idea.
                //if(executor.isEmpty() || pausedBatch) {
                //    if(!pausedBatch)batchX = playerChunk.x - (int) (renderDistance / 4);
                //    for (; batchX < playerChunk.x + (int) (renderDistance / 4); batchX++) {
                //        if(!pausedBatch)batchY = playerChunk.y - (int) (renderDistance / 8);
                //        for (; batchY < playerChunk.y + (int) (renderDistance / 8); batchY++) {
                //            if(!pausedBatch)batchZ = playerChunk.z - (int) (renderDistance / 8);
                //            for (; batchZ < playerChunk.z + (int) (renderDistance / 8); batchZ++) {
                //                pausedBatch = false;
    //
    //                        //load the chunk if it's in range
    //                        //distanceSquared is faster - just look at the code to find out why
    //                        temp.set(batchX, batchY, batchZ);
    //                        if (!render.hasChunk(temp) && RenderUtils.getChunkWorldPos(batchX, batchY, batchZ).distanceSquared(GlobalBits.playerPosition) < renderDistanceSquared) {
    //                            final int finalX = batchX;
    //                            final int finalY = batchY;
    //                            final int finalZ = batchZ;
    //                            executor.submit(new DistanceRunnable3i(() -> loadChunk(finalX, finalY, finalZ), new Vector3i(batchX, batchY, batchZ), playerChunk));
    //                        }
    //
    //                        if((r.getTime() - startTime) > targetTime) {
    //                            pausedBatch = true;
    //                            batchZ++;
    //                            return r.getTime() - startTime;
    //                        }

    //                    }
    //                }
    //            }
                //           pausedBatch = false;
                //       }
                //todo: batch pausing system needs more tuning, probably
            });
        return r.getTime() - startTime;
    }

    public void unloadChunks(Collection<GPUChunk> chunks){
        for(GPUChunk chunk: chunks){
            unloadChunk(chunk);
        }
    }
    public void unloadChunk(GPUChunk chunk){
        chunk.delete();
        //todo: world saves
    }

    /**
     * loads a chunk by either loading it from the world save, or generating it if it wasn't found in the save.
     * note: uses xyz chunk coordinates
     */
    public void loadChunk(int x, int y, int z){
        Vector3i pos = new Vector3i(x, y, z);

        if(render.getChunks().containsKey(pos)){
            RenderUtils.activeRender.printErrln("tried to load chunk that is already loaded! " + chunkIndex++);
            return;
        }
        //todo: world saves
        generateChunk(blocks, x, y, z);
    }

    private void generateChunk(Map<String, Block> blocks, int x, int y, int z){
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

        if(empty) render.spawnChunk(CHUNK_SIZE, null, x, y, z, false); //if it's empty, make an empty chunk.
        else render.spawnChunk(CHUNK_SIZE, blocksg, x, y, z, false);
    }

    public void close(){
        executor.shutdownNow();
        //todo: save chunks when world closes.
    }
}
