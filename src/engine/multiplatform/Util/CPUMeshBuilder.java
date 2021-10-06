package engine.multiplatform.Util;

import engine.multiplatform.model.CPUMesh;

import java.util.ArrayList;

public class CPUMeshBuilder {
    private final ArrayList<Float> positions;
    private final ArrayList<Float> textureCoordinates;
    private final ArrayList<Integer> indices;

    public CPUMeshBuilder(){
        this.positions = new ArrayList<>();
        this.textureCoordinates = new ArrayList<>();
        this.indices = new ArrayList<>();
    }

    public CPUMeshBuilder(int initialCapacity){
        this.positions = new ArrayList<>(initialCapacity);
        this.textureCoordinates = new ArrayList<>(initialCapacity);
        this.indices = new ArrayList<>(initialCapacity);
    }

    public void addBlockMesh(CPUMesh mesh, int x, int y, int z){
        int indexOffset = this.positions.size()/3;

        //STEP ONE: translate and mirror the block mesh and add those to the position buffer
        float mirror = ((x + z) & 1) - 0.5f; //it's upside down or not (-1 if it needs to be mirrored on the Y axis
        for(int i=0; i<mesh.positions.length/3; i++){
            this.positions.add(mesh.positions[3 * i    ] * 0.5f + x*0.288675134595f); //X position
            this.positions.add(mesh.positions[3 * i + 1] * 0.5f + y*0.5f); //Y position
            this.positions.add(mesh.positions[3 * i + 2] * mirror + z*0.5f); //z position
        }
        //STEP TWO: add texture coordinates (these don't change)
        for(float coord: mesh.UVCoords){
            this.textureCoordinates.add(coord);
        }
        //STEP THREE: modify indices and add them to the buffer
        for(int i = 0; i < mesh.indices.length; i++){

            indices.add(mesh.indices[i] + indexOffset);
        }
    }

    public void addBlockMesh(CPUMesh mesh, int x, int y, int z, byte blockedFaces){
        if((~blockedFaces & 0b11111) == 0){
            return; //if all the faces are blocked, just skip the voxel completely.
        }

        int indexOffset = this.positions.size()/3;
        float mirror = ((x + z) & 1) - 0.5f; //it's upside down or not (-1 if it needs to be mirrored on the Z axis)

        for(int i=0; i<mesh.positions.length/3; i++){
            this.positions.add(mesh.positions[3 * i    ] / 2f + x/3.465f); //X position
            this.positions.add(mesh.positions[3 * i + 1] / 2f + y/2f); //Y position
            this.positions.add(mesh.positions[3 * i + 2] * mirror + z/2f); //z position
        }
        //STEP TWO: add texture coordinates (these don't change)
        for(float coord: mesh.UVCoords){
            this.textureCoordinates.add(coord);
        }
        //STEP THREE: modify indices and add them to the buffer
        for (int tri = 0; tri < mesh.indices.length/3; tri++) {
            if(mesh.removableTriangles.length == 0) continue;
            if((mesh.removableTriangles[tri] & blockedFaces)!=0) {
                continue; // Skip this triangle if it should be removed
            }

            indices.add(mesh.indices[3 * tri    ] + indexOffset);
            indices.add(mesh.indices[3 * tri + 1] + indexOffset); //add the triangle's indices to the mesh
            indices.add(mesh.indices[3 * tri + 2] + indexOffset);
        }
    }

    public CPUMesh getMesh(){
        float[] positions = new float[this.positions.size()];
        float[] textureCoordinates = new float[this.textureCoordinates.size()];
        int[] indices = new int[this.indices.size()];

        //copy vertices into arrays
        for(int i=0; i<positions.length/3; i++){
            //for each vertex, plonk the data into the array
            positions[i*3  ] = this.positions.get(i*3  );
            positions[i*3+1] = this.positions.get(i*3+1);
            positions[i*3+2] = this.positions.get(i*3+2);

            textureCoordinates[i*2  ] = this.textureCoordinates.get(i*2  );
            textureCoordinates[i*2+1] = this.textureCoordinates.get(i*2+1);
        }
        //copy indices into array
        for(int i=0; i< indices.length; i++){
            indices[i] = this.indices.get(i);
        }

        return new CPUMesh(positions, textureCoordinates, indices);
    }
}
