package engine.multiplatform.render;

public interface GPUEntity {
    /**
     * 0: unknown
     * 1: GL33
     * 2: GL21
     * 3: DX9
     *
     * @return the integer ID of the render class this mesh belongs to.
     */
    int getRenderClass();
}
