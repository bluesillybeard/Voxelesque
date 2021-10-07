package engine.gl33.render;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram {

    //handles
    private final int programId;

    //uniforms
    private final int viewMatrixUniform, modelViewMatrixUniform, projectionMatrixUniform, textureSamplerUniform;
    private final int timeInSecondsUniform;

    public ShaderProgram(String vertexShaderCode, String fragmentShaderCode) throws Exception {

        this.programId = glCreateProgram();
        if (this.programId == 0) {
            throw new Exception("Could not create Shader");
        }

        int vertexShaderId = createShader(vertexShaderCode, GL_VERTEX_SHADER);
        int fragmentShaderId = createShader(fragmentShaderCode, GL_FRAGMENT_SHADER);

        //LINK SHADER PROGRAM
        glLinkProgram(this.programId);
        if (glGetProgrami(this.programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(this.programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(this.programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(this.programId, fragmentShaderId);
        }

        glValidateProgram(this.programId);
        if (glGetProgrami(this.programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(this.programId, 1024));
        }

        this.modelViewMatrixUniform = getUniformLocation("modelViewMatrix");
        this.projectionMatrixUniform = getUniformLocation("projectionMatrix");
        this.textureSamplerUniform = getUniformLocation("texture_sampler");
        this.viewMatrixUniform = getUniformLocation("viewMatrix");
        this.timeInSecondsUniform = getUniformLocation("timeSeconds");

    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(this.programId, shaderId);

        return shaderId;
    }

    public int getUniformLocation(String name){
        return glGetUniformLocation(this.programId, name);
    }

    public void setUniformMat4(int uniform, Matrix4f value){
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniform, false,
                    value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform1i(int uniform, int value){
        glUniform1i(uniform, value);
    }

    public void setUniform1f(int uniform, float value){
        glUniform1f(uniform, value);
    }

    public void setModelViewMatrix(Matrix4f value){
        setUniformMat4(modelViewMatrixUniform, value);
    }
    public void setProjectionMatrix(Matrix4f value){
        setUniformMat4(projectionMatrixUniform, value);

    }
    public void setViewMatrix(Matrix4f value){
        setUniformMat4(viewMatrixUniform, value);
    }
    public void setTextureSampler(int value) {
        setUniform1i(textureSamplerUniform, value);
    }

    public void setGameTime(){setUniform1f(timeInSecondsUniform, System.nanoTime() / 1_000_000_000f);}


    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
