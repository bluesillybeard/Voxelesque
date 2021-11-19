package engine.multiplatform.gpu;

public interface GPUObject {

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (for when
     * 1:GL33
     *
     *
     * @return the render backend ID
     */
    int getRenderType();
}
