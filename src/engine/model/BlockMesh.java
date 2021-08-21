package engine.model;

import java.util.Arrays;

public class BlockMesh{
    public float[] positions;
    public float[] UVCoords;
    public int[] indices;

    public int[] removableTriangles;
    public int[] blockedFaces;

    public BlockMesh(float[] positions, float[] UVCoords, int[] indices) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = new int[0];
        this.blockedFaces = new int[0];
    }

    public BlockMesh(float[] positions, float[] UVCoords, int[] indices, int[] removableTriangles, int[] blockedFaces) {
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
