package engine.multiplatform.model;

import VMF.VMFLoader;

import java.util.Arrays;

public class CPUMesh {
    public final float[] positions;
    public final float[] UVCoords;
    public final int[] indices;

    public final byte[] removableTriangles;
    public final byte blockedFaces;

    public CPUMesh(float[] positions, float[] UVCoords, int[] indices) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = new byte[0];
        this.blockedFaces = 0;
        System.out.println("create CPU mesh " + this.hashCode() + "|" + positions.length + "|" + indices.length);
    }
    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    public CPUMesh(float[] positions, float[] UVCoords, int[] indices, byte[] removableTriangles, byte blockedFaces) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = removableTriangles;
        this.blockedFaces = blockedFaces;
        System.out.println("create CPU mesh " + this.hashCode() + "|" + positions.length + "|" + indices.length);
    }

    public CPUMesh(VMFLoader loader){
        this.positions = loader.getVertices();
        this.UVCoords = loader.getTextureCoordinates();
        this.indices = loader.getIndices();
        this.removableTriangles = loader.getRemovableTriangles();
        this.blockedFaces = loader.getBlockedFaces();
        System.out.println("create CPU mesh " + this.hashCode() + "|" + positions.length + "|" + indices.length);
    }
    public CPUMesh clone() {
        try {
            super.clone();
            System.err.println("CPUMesh super.clone() didn't throw an exception, meaning it extends a cloneable class");
        } catch (CloneNotSupportedException ignored) {}
        return new CPUMesh(Arrays.copyOf(positions, positions.length), Arrays.copyOf(UVCoords, UVCoords.length), Arrays.copyOf(indices, indices.length), Arrays.copyOf(removableTriangles, removableTriangles.length), blockedFaces);
    }
}
