package engine.model;

/**
 * same as a normal mesh, but it also stores some data that is used during chunk construction.
 */
public class BlockMesh{
    float[] positions;
    float[] UVCoords;
    int[] indices;
    int[] removableTriangles;
    int[] blockableTriangles;
    public BlockMesh(float[] positions, float[] UVCoords, int[] indices) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = new int[0];
        this.blockableTriangles = new int[0];
    }
    public BlockMesh(float[] positions, float[] UVCoords, int[] indices, int[] removableTriangles, int[] blockableTriangles) {
        this.positions = positions;
        this.UVCoords = UVCoords;
        this.indices = indices;
        this.removableTriangles = removableTriangles;
        this.blockableTriangles = blockableTriangles;
    }

}
