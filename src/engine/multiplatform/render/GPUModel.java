package engine.multiplatform.render;

public interface GPUModel {
    /**
     * 0: unknown
     * 1: GL33
     * 2: GL21
     * 3: DX9
     * @return the integer ID of the render class this model belongs to.
     */
    int getRenderClass();
}
