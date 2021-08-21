package engine.util;

import engine.model.BlockMesh;
import engine.model.Mesh;

import java.util.ArrayList;

public class BlockMeshBuilder {
    ArrayList<Float> positions;
    ArrayList<Float> textureCoordinates;
    ArrayList<Integer> indices;

    public void addBlockMesh(BlockMesh mesh, int x, int y, int z){
        int indexOffset = this.positions.size()/3;

        //STEP ONE: translate and mirror the block mesh and add those to the position buffer
        int mirror = -((x + y) & 1); //it's upside down or not (-1 if it needs to be mirrored on the Y axis
        for(int i=0; i<mesh.positions.length/3; i++){
            this.positions.add(mesh.positions[3 * i    ] * 0.5f * x); //X position
            this.positions.add(mesh.positions[3 * i + 1] * mirror * 0.5f * y); //Y position
            this.positions.add(mesh.positions[3 * i + 2] * 0.5f * z); //z position
        }
        //STEP TWO: add texture coordinates (these don't change)
        for(float coord: mesh.UVCoords){
            this.textureCoordinates.add(coord);
        }
        //STEP THREE: modify indices and add them to the buffer
        for(int index: mesh.indices){
            indices.add(index + indexOffset);
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
