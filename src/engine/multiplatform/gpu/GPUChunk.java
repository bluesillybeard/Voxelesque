package engine.multiplatform.gpu;

public interface GPUChunk extends GPUObject{

    /**
     * sets the block data of this chunk.
     * @param blocks a 3D array of blockModels that represent that chunk's block data.
     * @param buildImmediately weather the chunk will be rebuilt immediately (true) or placed in priority queue (false)
     */
    void setData(GPUBlock[][][] blocks, boolean buildImmediately);

    /**
     * sets a specific block [z, y, x] of a chunk.
     * @param block the blockModel to be used
     * @param buildImmediately weather the chunk will be rebuilt immediately (true) or placed in priority queue (false)
     */
    void setBlock(GPUBlock block, int x, int y, int z, boolean buildImmediately);

    /**
     * Deletes this chunk, so it will no longer be updated or rendered.
     * changing blocks in a deleted chunk causes undefined behavior, so don't do it!
     */
    void delete();
}
