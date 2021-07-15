package engine;

import engine.graph.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.graph.Mesh;

public class GameItem {

    private final Mesh mesh;
    
    private final Vector3f position;
    
    private float scale;

    private final ShaderProgram shaderProgram;

    private final Matrix4f modelViewMatrix;

    private final Vector3f rotation;

    public GameItem(Mesh mesh, ShaderProgram shaderProgram) {
        this.mesh = mesh;
        this.shaderProgram = shaderProgram;
        position = new Vector3f();
        scale = 1;
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

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }

    public void render(){

        shaderProgram.bind();
        // Set model view matrix for this item
        modelViewMatrix.identity().translate(position).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(scale);
        shaderProgram.setModelViewMatrix(modelViewMatrix);

        mesh.render();
        shaderProgram.unbind();
    }
}