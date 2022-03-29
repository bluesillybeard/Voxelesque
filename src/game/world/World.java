package game.world;

import engine.multiplatform.Render;
import engine.multiplatform.RenderUtils;
import engine.multiplatform.gpu.GPUChunk;
import game.GlobalBits;
import game.world.block.Block;
import org.lwjgl.system.CallbackI;
import util.noise.FastNoiseLite;
import org.joml.Vector3i;
import util.threads.DistanceRunnable3i;
import util.threads.PriorityThreadPoolExecutor;

import java.util.*;

import static game.GlobalBits.*;

public class World {

    int num;
    private final FastNoiseLite noise;
    private final ArrayList<GPUChunk> chunksToUnload;


    private boolean pausedBatch;
    private int batchX;
    private int batchY;
    private int batchZ;
    private final PriorityThreadPoolExecutor<DistanceRunnable3i> executor = new PriorityThreadPoolExecutor<>(DistanceRunnable3i.inOrder, Runtime.getRuntime().availableProcessors());

    public static final int CHUNK_SIZE = 16; //MUST BE A POWER OF 2! If this is changed to a non-power of 2, many things would have to be reworked.

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
        Iterator<Map.Entry<Vector3i, GPUChunk>> iter = RenderUtils.activeRender.getChunks().entrySet().iterator();
        //todo: finish implementing method
        chunksToUnload.clear();
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


    public double updateChunks(double targetTime){
        final Render r = RenderUtils.activeRender;
        final double startTime = r.getTime();
        final float renderDistanceSquared = renderDistance * renderDistance;
        final Vector3i temp = new Vector3i();


        render.getChunks().forEach((pos, chunk) -> {
            if (RenderUtils.getChunkWorldPos(pos).distanceSquared(GlobalBits.playerPosition) > renderDistanceSquared) {
                chunksToUnload.add(chunk);
            }
        });

        unloadChunks(chunksToUnload);

        chunksToUnload.clear();
        if(executor.isEmpty() || pausedBatch) {
            if(!pausedBatch)batchX = playerChunk.x - (int) (renderDistance / 4);
            for (; batchX < playerChunk.x + (int) (renderDistance / 4); batchX++) {
                if(!pausedBatch)batchY = playerChunk.y - (int) (renderDistance / 8);
                for (; batchY < playerChunk.y + (int) (renderDistance / 8); batchY++) {
                    if(!pausedBatch)batchZ = playerChunk.z - (int) (renderDistance / 8);
                    for (; batchZ < playerChunk.z + (int) (renderDistance / 8); batchZ++) {
                        pausedBatch = false;

                        //load the chunk if it's in range
                        //distanceSquared is faster - just look at the code to find out why
                        temp.set(batchX, batchY, batchZ);
                        if (!render.hasChunk(temp) && RenderUtils.getChunkWorldPos(batchX, batchY, batchZ).distanceSquared(GlobalBits.playerPosition) < renderDistanceSquared) {
                            final int finalX = batchX;
                            final int finalY = batchY;
                            final int finalZ = batchZ;
                            executor.submit(new DistanceRunnable3i(() -> loadChunk(finalX, finalY, finalZ), new Vector3i(batchX, batchY, batchZ), playerChunk));
                        }

                        if((r.getTime() - startTime) > targetTime) {
                            pausedBatch = true;
                            batchZ++;
                            return r.getTime() - startTime;
                        }

                    }
                }
            }
            pausedBatch = false;
        }
        //todo: batch pausing system needs more tuning, probably

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
            RenderUtils.activeRender.printErrln("tried to load chunk that is already loaded! " + num++);
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
        executor.stop();
        //todo: save chunks when world closes.
    }
}
