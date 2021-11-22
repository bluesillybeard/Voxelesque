package engine.multiplatform.gpu;


import engine.multiplatform.model.CPUMesh;

public interface GPUBlock{
    GPUShader getShader();

    GPUTexture getTexture();

    CPUMesh getMesh();
}
