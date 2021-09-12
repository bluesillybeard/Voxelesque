package engine.model;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final int vaoId;
    private final int posVboId;
    private final int UVVboId;
    private final int idxVboId;
    private final int vertexCount;

    private final float[] positions; //these exist only so the CPU can look at them when it needs them
    private final float[] UVCoords;
    private final int[] indices;

    public Mesh(float[] positions, float[] UVCoords, int[] indices, boolean storeOnCPU) {
        if(storeOnCPU) {
            this.positions = positions;
            this.indices = indices;
            this.UVCoords = UVCoords;
        } else {
            this.positions = null;
            this.indices = null;
            this.UVCoords = null;
        }
        FloatBuffer posBuffer = null;
        FloatBuffer UVBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            this.vertexCount = indices.length;

            this.vaoId = glGenVertexArrays();
            glBindVertexArray(this.vaoId);

            // Position VBO
            this.posVboId = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.posVboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // UV VBO
            this.UVVboId = glGenBuffers();
            UVBuffer = MemoryUtil.memAllocFloat(UVCoords.length);
            UVBuffer.put(UVCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.UVVboId);
            glBufferData(GL_ARRAY_BUFFER, UVBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            this.idxVboId = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (UVBuffer != null) {
                MemoryUtil.memFree(UVBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public Mesh(BlockMesh mesh, boolean storeOnCPU) {
        if(storeOnCPU) {
            this.positions = mesh.positions;
            this.indices = mesh.indices;
            this.UVCoords = mesh.UVCoords;
        } else {
            this.positions = null;
            this.indices = null;
            this.UVCoords = null;
        }

        FloatBuffer posBuffer = null;
        FloatBuffer UVBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            this.vertexCount = indices.length;

            this.vaoId = glGenVertexArrays();
            glBindVertexArray(this.vaoId);

            // Position VBO
            this.posVboId = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.posVboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // UV VBO
            this.UVVboId = glGenBuffers();
            UVBuffer = MemoryUtil.memAllocFloat(UVCoords.length);
            UVBuffer.put(UVCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, this.UVVboId);
            glBufferData(GL_ARRAY_BUFFER, UVBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            this.idxVboId = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (UVBuffer != null) {
                MemoryUtil.memFree(UVBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public int getVaoId() {
        return this.vaoId;
    }
    public float[] getPositions(){return this.positions;}
    public int[] getIndices(){return this.indices;}
    public float[] getUVCoords(){return this.UVCoords;}

    public void render() {
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("\npositions:\n");
        for(int i=0; i < positions.length/3; i++){
            str.append("    ");
            str.append(positions[i*3]);
            str.append(", ");
            str.append(positions[i*3+1]);
            str.append(", ");
            str.append(positions[i*3+2]);
            str.append(",\n");
        }
        str.append("\ntextureCoordinates:\n");
        for(int i=0; i<UVCoords.length/2; i++){
            str.append("    ");
            str.append(UVCoords[i*2]);
            str.append(", ");
            str.append(UVCoords[i*2+1]);
            str.append(",\n");
        }
        str.append("\nindices:\n");
        for(int i=0; i<indices.length/3; i++){
            str.append("    ");
            str.append(indices[i*3]);
            str.append(", ");
            str.append(indices[i*3+1]);
            str.append(", ");
            str.append(indices[i*3+2]);
            str.append(",\n");
        }
        return str.toString();
    }
    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(this.posVboId);
        glDeleteBuffers(this.UVVboId);
        glDeleteBuffers(this.idxVboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(this.vaoId);
    }
}
