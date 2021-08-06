package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GameItem {

    private final Vector3f position;
    
    private final Vector3f scale;

    private final ShaderProgram shaderProgram;

    private final Matrix4f modelViewMatrix;

    private final Model model;

    private final Vector3f rotation;

    public GameItem(Mesh mesh, ShaderProgram shaderProgram, Texture texture) {
        this.model = new Model(mesh, texture);
        this.shaderProgram = shaderProgram;
        position = new Vector3f();
        scale = new Vector3f();
        rotation = new Vector3f();
        modelViewMatrix = new Matrix4f();
    }

    public GameItem(Model model, ShaderProgram shaderProgram) {
        this.model = model;
        this.shaderProgram = shaderProgram;
        position = new Vector3f();
        scale = new Vector3f();
        rotation = new Vector3f();
        modelViewMatrix = new Matrix4f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void render(){

        shaderProgram.bind();
        // Set model view matrix for this item
        modelViewMatrix.identity().translate(position).
                rotateX(rotation.x).
                rotateY(rotation.y).
                rotateZ(rotation.z).
                scale(scale);
        shaderProgram.setModelViewMatrix(modelViewMatrix);

        model.render();
        shaderProgram.unbind();

    }
}