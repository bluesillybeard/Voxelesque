package engine.gl33.render;

import engine.gl33.model.GPUMesh;
import engine.gl33.model.GPUModel;
import engine.gl33.model.GPUTexture;
import engine.gl33.model.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderableEntity {

    private final boolean hidden;

    private final Vector3f position;

    private final Vector3f scale;

    private ShaderProgram shaderProgram;

    private final Matrix4f modelViewMatrix;

    private GPUModel model;

    private final Vector3f rotation;

    public RenderableEntity(GPUMesh mesh, ShaderProgram shaderProgram, GPUTexture texture) {
        this.model = new GPUModel(mesh, texture);
        this.shaderProgram = shaderProgram;
        this.position = new Vector3f();
        this.scale = new Vector3f();
        this.rotation = new Vector3f();
        this.modelViewMatrix = new Matrix4f();
        hidden = false;
        updateViewMatrix();
    }

    public RenderableEntity(GPUModel model, ShaderProgram shaderProgram) {
        this.model = model;
        this.shaderProgram = shaderProgram;
        this.position = new Vector3f();
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f();
        this.modelViewMatrix = new Matrix4f();
        hidden = false;
        updateViewMatrix();
    }
    private void updateViewMatrix(){
        this.modelViewMatrix.identity().translate(this.position).
                rotateX(-this.rotation.x).
                rotateY(-this.rotation.y).
                rotateZ(-this.rotation.z).
                scale(this.scale);
    }
    public GPUModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        updateViewMatrix();
    }

    public Vector3f getScale() {
        return this.scale;
    }

    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
        updateViewMatrix();
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        updateViewMatrix();
    }

    public void setModel(GPUModel model){
        this.model = model;
    }

    public void setModel(GPUMesh mesh, GPUTexture texture){
        if(this.model.mesh != mesh || this.model.texture != texture) { //if the model or mesh are different
            this.model = new GPUModel(mesh, texture);
        }
    }

    public void setShaderProgram(ShaderProgram program){
        this.shaderProgram = program;
    }

    public void render(){
        if(!hidden) {
            this.shaderProgram.bind();

            // Set model view matrix for this item
            this.shaderProgram.setModelViewMatrix(this.modelViewMatrix);

            this.model.render();
        }
    }
}