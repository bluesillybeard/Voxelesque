package engine.multiplatform.model;

import java.awt.image.BufferedImage;

public class CPUModel {
    public final CPUMesh mesh;
    public final BufferedImage texture;

    public CPUModel(CPUMesh mesh, BufferedImage texture){
        this.mesh = mesh;
        this.texture = texture;

    }
}
