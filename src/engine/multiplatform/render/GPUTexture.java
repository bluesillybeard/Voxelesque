package engine.multiplatform.render;

public interface GPUTexture {
    /**
     * 0: unknown
     * 1: GL33
     * 2: GL21
     * 3: DX9
     * @return the integer ID of the render class this texture belongs to.
     */
    int getRenderClass();
}
