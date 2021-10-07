package engine.gl33.render;

import engine.gl33.model.GPUMesh;
import engine.gl33.model.GPUModel;
import engine.gl33.model.GPUTexture;

public class RenderableTextEntity extends RenderableEntity{
    //TODO: you started on text rendering.
    public RenderableTextEntity(GPUMesh mesh, ShaderProgram shaderProgram, GPUTexture texture) {
        super(mesh, shaderProgram, texture);
    }

    public RenderableTextEntity(GPUModel model, ShaderProgram shaderProgram) {
        super(model, shaderProgram);
    }
}
