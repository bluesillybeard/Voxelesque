package engine.gl33.render;

import engine.multiplatform.RenderUtils;
import engine.multiplatform.gpu.GPUShader;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class GL33Shader implements GPUShader {

    //handles
    private final int programId;

    //uniforms
    private final int viewMatrixUniform, modelViewMatrixUniform, projectionMatrixUniform, textureSamplerUniform;
    private final int timeInSecondsUniform;

    public GL33Shader(String vertexShaderCode, String fragmentShaderCode) throws Exception {

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

    public void setUniformVec3(int uniform, Vector3f value){
        // Dump the vector into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3fv(uniform,
                    value.get(stack.mallocFloat(3)));
        }
    }

    public void setUniformVec4(int uniform, Vector4f value){
        // Dump the vector into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3fv(uniform,
                    value.get(stack.mallocFloat(4)));
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

    public void setGameTime(double time){setUniform1f(timeInSecondsUniform, (float)time);}


    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void delete() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public int hashCode(){
        return programId;
    }
    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (for when
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }

    /**
     * currently supported types:
     * -Matrix4f
     * -Vector3f
     * -Vector4f
     * @param uniform the uniform to set the value of.
     * @param value the new value of the uniform
     */

    @Override
    public void setUniform(String uniform, Object value) {
        int uniformId = glGetUniformLocation(this.programId, uniform);
        if(uniformId < 1) RenderUtils.activeRender.printErrln("uniform " + uniform + " does not exist; tried to send value " + value + " type " + value.getClass());
        else if(value instanceof Number){
            RenderUtils.activeRender.printErrln("Tried to send number through generic setUniform");
        } else if(value instanceof Matrix4f){
            setUniformMat4(uniformId, (Matrix4f) value);
        } else if(value instanceof Vector3f){
            setUniformVec3(uniformId, (Vector3f) value);
        } else if(value instanceof Vector4f){
            setUniformVec4(uniformId, (Vector4f) value);
        }
    }

    @Override
    public void setUniform(String uniform, float value) {
        int uniformId = glGetUniformLocation(this.programId, uniform);
        if(uniformId < 1) RenderUtils.activeRender.printErrln("uniform " + uniform + " does not exist; tried to send value " + value + " type float");
        else setUniform1f(uniformId, value);
    }

    @Override
    public void setUniform(String uniform, int value) {
        int uniformId = glGetUniformLocation(this.programId, uniform);
        if(uniformId < 1) RenderUtils.activeRender.printErrln("uniform " + uniform + " does not exist; tried to send value " + value + " type int");
        else setUniform1i(uniformId, value);
    }
}
