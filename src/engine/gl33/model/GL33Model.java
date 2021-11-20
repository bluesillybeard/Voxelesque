package engine.gl33.model;

import engine.multiplatform.gpu.GPUMesh;
import engine.multiplatform.gpu.GPUModel;
import engine.multiplatform.gpu.GPUTexture;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;

import java.awt.image.BufferedImage;

public class GL33Model implements GPUModel {
    public final GL33Mesh mesh;
    public final GL33Texture texture;

    public GL33Model(GL33Mesh mesh, GL33Texture texture){
        this.mesh = mesh;
        this.texture = texture;
    }
    public GL33Model(CPUMesh mesh, BufferedImage texture){
        this.mesh = new GL33Mesh(mesh);
        this.texture = new GL33Texture(texture);
    }
    public GL33Model(CPUModel model){
        this(model.mesh, model.texture);
    }

    public void render(){
        texture.bind();
        mesh.render();
    }

    @Override
    public GPUMesh getMesh() {
        return mesh;
    }

    @Override
    public GPUTexture getTexture() {
        return texture;
    }

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (for when
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }
}
