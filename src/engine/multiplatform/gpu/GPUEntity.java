package engine.multiplatform.gpu;

import org.joml.Vector3f;

public interface GPUEntity extends GPUObject{
    void setLocation(Vector3f pos);
    void setLocation(float x, float y, float z);

    void setRotation(Vector3f rot);
    void setRotation(float x, float y, float z);

    void setScale(Vector3f scale);
    void setScale(float x, float y, float z);

    void setPosition(Vector3f location, Vector3f rotation, Vector3f scale);
    void setPosition(float xLocation, float yLocation, float zLocation, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);


}
