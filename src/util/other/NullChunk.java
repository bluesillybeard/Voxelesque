package util.other;

import engine.multiplatform.gpu.GPUBlock;
import engine.multiplatform.gpu.GPUChunk;
import org.joml.Vector3i;

public class NullChunk implements GPUChunk {

    private final Vector3i pos;

    public NullChunk(Vector3i pos) {
        this.pos = pos;
    }


    @Override
    public Vector3i getPos() {
        return pos;
    }

    /**
     * sets the block data of this chunk.
     *
     * @param blocks           a 3D array of blockModels that represent that chunk's block data.
     * @param buildImmediately weather the chunk will be rebuilt immediately (true) or placed in priority queue (false)
     */
    @Override
    public void setData(GPUBlock[][][] blocks, boolean buildImmediately) {

    }

    /**
     * sets a specific block [z, y, x] of a chunk.
     *
     * @param block            the blockModel to be used
     * @param buildImmediately weather the chunk will be rebuilt immediately (true) or placed in priority queue (false)
     */
    @Override
    public void setBlock(GPUBlock block, int x, int y, int z, boolean buildImmediately) {

    }

    @Override
    public GPUBlock getBlock(int x, int y, int z) {
        return null;
    }

    /**
     * Deletes this chunk, so it will no longer be updated or rendered.
     * changing blocks in a deleted chunk causes undefined behavior, so don't do it!
     */
    @Override
    public void delete() {

    }

    /**
     * Nope.
     * @return 0;
     *
     */
    @Override
    public int getRenderType() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof GPUChunk c) {
            return this.pos.equals(c.getPos());
        } else {
            return false;
        }
    }
}
