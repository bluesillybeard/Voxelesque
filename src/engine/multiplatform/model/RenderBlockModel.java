package engine.multiplatform.model;

import engine.multiplatform.render.GPUShader;
import engine.multiplatform.render.GPUTexture;

/**
 * Makes transferring blocks between the engine and game easier.
 */
public interface RenderBlockModel {
    static RenderBlockModel newModel(CPUMesh mesh, GPUTexture texture, GPUShader shader){
        return new RenderBlockModel() {
            @Override
            public CPUMesh getMesh() {
                return mesh;
            }
            @Override
            public GPUTexture getTexture() {
                return texture;
            }
            @Override
            public GPUShader getShader() {
                return shader;
            }
        };
    }
    CPUMesh getMesh();
    GPUTexture getTexture();
    GPUShader getShader();
}
