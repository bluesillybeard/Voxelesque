package engine.gl33.model;

import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;

import java.awt.image.BufferedImage;

public class GPUModel {
    public final GPUMesh mesh;
    public final GPUTexture texture;

    public GPUModel(GPUMesh mesh, GPUTexture texture){
        this.mesh = mesh;
        this.texture = texture;
    }
    public GPUModel(CPUMesh mesh, BufferedImage texture){
        this.mesh = new GPUMesh(mesh);
        this.texture = new GPUTexture(texture);
    }
    public GPUModel(CPUModel model){
        this(model.mesh, model.texture);
    }
}
