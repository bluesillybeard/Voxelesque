package engine.model;

import java.util.Arrays;

public class BlockMesh{
    public float[] positions;
    public float[] UVCoords;
    public int[] indices;

    public int[] removableTriangles;
    public boolean[] blockedFaces;

    public BlockMesh(float[] positions, float[] UVCoords, int[] indices) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = new int[0];
        this.blockedFaces = new boolean[0];
    }
    //blockedFaces: [top (+y), bottom(-y), (-z / +z), -x, +x]
    public BlockMesh(float[] positions, float[] UVCoords, int[] indices, int[] removableTriangles, boolean[] blockedFaces) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = removableTriangles;
        this.blockedFaces = blockedFaces;
    }

    public BlockMesh clone(){
        return new BlockMesh(Arrays.copyOf(positions, positions.length), Arrays.copyOf(UVCoords, UVCoords.length), Arrays.copyOf(indices, indices.length));
    }

}
