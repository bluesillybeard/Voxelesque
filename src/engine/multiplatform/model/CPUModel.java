package engine.multiplatform.model;

import VMF.VMFLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class CPUModel {
    public final CPUMesh mesh;
    public final BufferedImage texture;

    public CPUModel(CPUMesh mesh, BufferedImage texture){
        this.mesh = mesh;
        this.texture = texture;

    }

    public CPUModel(VMFLoader load) throws IOException {
        this.mesh = new CPUMesh(load);
        this.texture = load.getImage();
    }

}
