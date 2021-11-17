package engine.gl33.model;

import engine.multiplatform.model.CPUMesh;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GL33Mesh {

    private final int vaoId;
    private final int posVboId;
    private final int UVVboId;
    private final int idxVboId;
    private final int vertexCount;

    public GL33Mesh(float[] positions, float[] UVCoords, int[] indices) {
        this.vertexCount = indices.length;
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.posVboId = sendFloats(positions, 3, 0);
        this.UVVboId = sendFloats(UVCoords, 2, 1);
        this.idxVboId = sendIndices(indices);
    }

    public GL33Mesh(CPUMesh mesh) {
        this.vertexCount = mesh.indices.length;
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.posVboId = sendFloats(mesh.positions, 3, 0);
        this.UVVboId = sendFloats(mesh.UVCoords, 2, 1);
        this.idxVboId = sendIndices(mesh.indices);
    }
    private int sendFloats(float[] values, int size, int attribIndex){
        int id = glGenBuffers();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(values.length);
        buffer.put(values).flip();
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(attribIndex);
        glVertexAttribPointer(attribIndex, size, GL_FLOAT, false, 0, 0);

        MemoryUtil.memFree(buffer);
        return id;
    }

    private int sendIndices(int[] values){
        int id = glGenBuffers();
        IntBuffer intBuffer = MemoryUtil.memAllocInt(values.length);
        intBuffer.put(values).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);

        MemoryUtil.memFree(intBuffer);
        return id;
    }

    public void render() {
        // Draw the mesh
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
    }

    public String toString(){
        return "GPUMesh ID " + vaoId + " has " + vertexCount + " vertices";
    }

    public void cleanUp() {
        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(this.posVboId);
        glDeleteBuffers(this.UVVboId);
        glDeleteBuffers(this.idxVboId);

        // Delete the VAO
        glDeleteVertexArrays(this.vaoId);
    }
}
