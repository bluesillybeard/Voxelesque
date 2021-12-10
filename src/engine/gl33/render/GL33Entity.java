package engine.gl33.render;

import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Model;
import engine.gl33.model.GL33Texture;
import engine.multiplatform.gpu.GPUEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public class GL33Entity implements GPUEntity, Comparable<GL33Entity> {

    private static int ID;
    private final int id;
    private final boolean hidden;

    private final Vector3f position;
    private final Vector3f scale;
    private final Vector3f rotation;

    private final Matrix4f modelViewMatrix;

    private GL33Shader shaderProgram;
    private GL33Model model;

    public GL33Entity(GL33Mesh mesh, GL33Shader shaderProgram, GL33Texture texture) {
        this(new GL33Model(mesh, texture), shaderProgram);
    }

    public GL33Entity(GL33Model model, GL33Shader shaderProgram) {
        this.id = ID++;
        this.model = model;
        this.shaderProgram = shaderProgram;
        this.position = new Vector3f();
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f();
        this.modelViewMatrix = new Matrix4f();
        hidden = false;
        updateViewMatrix();
    }


    public Matrix4f getModelViewMatrix(){
        return modelViewMatrix;
    }

    private void updateViewMatrix(){
        this.modelViewMatrix.identity().translate(this.position).
                rotateX(-this.rotation.x).
                rotateY(-this.rotation.y).
                rotateZ(-this.rotation.z).
                scale(this.scale);
    }
    public GL33Model getModel() {
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

    public void setModel(GL33Model model){
        this.model = model;
    }

    public void setModel(GL33Mesh mesh, GL33Texture texture){


        if(this.model.mesh != mesh || this.model.texture != texture) { //if the model or mesh are different
            this.model = new GL33Model(mesh, texture);
        }
    }

    public void setShaderProgram(GL33Shader program){
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

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (This should absolutely under no circumstances ever happen. Not in all time and space should this value ever be returned by this function)
     * 1:GL33
     *
     * @return the render backend ID
     */
    @Override
    public int getRenderType() {
        return 1;
    }

    public int hashCode(){
        return Objects.hash(id);
    }

    /**
     * @param o the other GL33Entity.
     * @return this.hashCode() - o.hashCode()
     */
    @Override
    public int compareTo(GL33Entity o) {
        return this.hashCode() - o.hashCode();
    }
}