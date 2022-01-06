package engine.multiplatform.gpu;

//this class exists solely to make OpenGL stuff more object-oriented
public interface GPUShader extends GPUObject {
    void setUniform(String uniform, Object value);
    void setUniform(String uniform, float value);
    void setUniform(String uniform, int value);


    void delete();


}
