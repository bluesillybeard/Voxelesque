package engine.multiplatform.Util;

import engine.multiplatform.model.CPUMesh;

import java.util.ArrayList;
import java.util.List;

public class CPUMeshBuilder {
    private final List<Vertex> vertices;
    private final List<Integer> indices;


    public CPUMeshBuilder(){
        vertices = new ArrayList<>();
        this.indices = new ArrayList<>();

    }

    public void addBlockMeshToChunk(CPUMesh mesh, int x, int y, int z, byte blockedFaces){
        if((~blockedFaces & 0b11111) == 0){
            return; //if all the faces are blocked, just skip the voxel completely.
        }
        float mirror = ((x + z) & 1) - 0.5f; //it's upside down or not (-1 if it needs to be mirrored on the Z axis)
        float[] posits = mesh.positions;
        float[] UVCoords = mesh.UVCoords;
        int[] indices = mesh.indices;
        byte[] removable = mesh.removableTriangles;
        //for each index in the meshes indices
        for (int i = 0; i < indices.length; i++) {
            if ((removable[i / 3] & blockedFaces) != 0) {
                continue; // Skip this index if it should be removed
            }
            int ind = indices[i];
            //get its vertex
            Vertex vertex = new Vertex(
                    posits[3 * ind] * 0.5f + x * 0.288675134595f,
                    posits[3 * ind + 1] * 0.5f + y * 0.5f,
                    posits[3 * ind + 2] * mirror + z * 0.5f,
                    UVCoords[2 * ind],
                    UVCoords[2 * ind + 1]
            );
            int index = vertices.indexOf(vertex);
            if (index != -1) {
                //if that vertex already exist, add its index
                this.indices.add(index);
            } else {
                //if it doesn't exist, add it and add its index
                this.vertices.add(vertex);
                this.indices.add(vertices.size() - 1);
            }
        }
    }

    public CPUMesh getMesh(){
        float[] positions = new float[vertices.size()*3];
        float[] UVCoords = new float[vertices.size()*2];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            positions[3 * i    ] = vertex.x;
            positions[3 * i + 1] = vertex.y;
            positions[3 * i + 2] = vertex.z;
            UVCoords [2 * i    ] = vertex.tx;
            UVCoords [2 * i + 1] = vertex.ty;
        }
        int[] indices = new int[this.indices.size()];
        List<Integer> integers = this.indices;
        for (int i = 0; i < integers.size(); i++) {
            indices[i] = this.indices.get(i);
        }
        return new CPUMesh(positions, UVCoords, indices);
    }

    private static class Vertex{
        public Vertex(float x, float y, float z, float tx, float ty) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.tx = tx;
            this.ty = ty;
        }

        public float x;
        public float y;
        public float z;
        public float tx;
        public float ty;
    }
}
