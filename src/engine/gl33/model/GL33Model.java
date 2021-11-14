package engine.gl33.model;

import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;
import engine.multiplatform.render.GPUModel;

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
        if(texture != null)texture.bind();
        mesh.render();
    }

    /**
     * 0: unknown
     * 1: GL33
     * 2: GL21
     * 3: DX9
     *
     * @return the integer ID of the render class this mesh belongs to.
     */
    @Override
    public int getRenderClass() {
        return 1;
    }
}
