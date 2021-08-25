package engine.util;

import engine.model.BlockMesh;
import engine.model.Mesh;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockMeshBuilder {
    private final ArrayList<Float> positions;
    private final ArrayList<Float> textureCoordinates;
    private final ArrayList<Integer> indices;
    public BlockMeshBuilder(){
        this.positions = new ArrayList<>();
        this.textureCoordinates = new ArrayList<>();;
        this.indices = new ArrayList<>();
    }

    public void addBlockMesh(BlockMesh mesh, int x, int y, int z){
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

    public void addBlockMesh(BlockMesh mesh, int x, int y, int z, boolean[] blockedFaces){
        if(blockedFaces[0] && blockedFaces[1] && blockedFaces[2] && blockedFaces[3] && blockedFaces [4]){
            return; //if all the faces are blocked, just skip the block completely.
        }

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

    public Mesh getMesh(){
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

        return new Mesh(positions, textureCoordinates, indices);
    }
}
