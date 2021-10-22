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

    /**
     * NOTICE: does not clone the contained image, only the mesh is cloned.
     * This means if you edit the image of the clone, it edits the image of the original and vice-versa.
     * @return the cloned model
     */
    public CPUModel clone() {
        try {
            super.clone();
            System.err.println("CPUModel super.clone() didn't throw an exception, meaning it extends a cloneable class");
        } catch (CloneNotSupportedException ignored) {}
        return new CPUModel(mesh.clone(), texture);
    }

}
