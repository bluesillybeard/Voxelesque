package engine.render;

import engine.model.Mesh;
import engine.model.Model;
import engine.model.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderableEntity {

    private final boolean hidden;

    private final Vector3f position;
    
    private final Vector3f scale;

    private ShaderProgram shaderProgram;

    private final Matrix4f modelViewMatrix;

    private Model model;

    private final Vector3f rotation;

    public RenderableEntity(Mesh mesh, ShaderProgram shaderProgram, Texture texture) {
        this.model = new Model(mesh, texture);
        this.shaderProgram = shaderProgram;
        this.position = new Vector3f();
        this.scale = new Vector3f();
        this.rotation = new Vector3f();
        this.modelViewMatrix = new Matrix4f();
        hidden = false;
        updateViewMatrix();
    }

    public RenderableEntity(Model model, ShaderProgram shaderProgram) {
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
    public Model getModel() {
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

    public void setModel(Model model){
        this.model = model;
    }

    public void setModel(Mesh mesh, Texture texture){
        if(this.model.getMesh() != mesh || this.model.getTexture() != texture) { //if the model or mesh are different
            this.model = new Model(mesh, texture);
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

    /**
     * @param y the Y screen coordinate (-1.0 - 1.0)
     * @param x the X screen coordinate (-1.0 - 1.0)
     * @param viewMatrix the viewMatrix - contains the camera position and rotation
     * @param projectionMatrix the projectionMatrix - contains the 3D perspective
     * @return true or false, depending on weather this function thinks that the GameItem touches the position on screen
     */
    public boolean touchesPositionOnScreen(float y, float x, Matrix4f viewMatrix, Matrix4f projectionMatrix){
        y = -y; //The screen coordinates are mirrored for some reason

        Matrix4f MVP;
        if(projectionMatrix != null && viewMatrix != null) {
            MVP = projectionMatrix.mul(viewMatrix, new Matrix4f()).mul(this.modelViewMatrix);
        }
        else
            MVP = this.modelViewMatrix; //when the camera and projection are not used, useful for GUI.

        int[] indices = this.model.getMesh().getIndices();
        float[] positions = this.model.getMesh().getPositions();
        // Go through each triangle
        // translate it to 2D coordinates
        //see if it collides with the position:
        //  if it does, return true
        //  if it doesn't, continue.
        //if none of the triangles collide, return false.
        for(int i=0; i<indices.length/3; i++){ //each triangle in the mesh
            //get that triangle
            Vector4f point1 = new Vector4f(
                    positions[3*indices[3*i  ]],
                    positions[3*indices[3*i  ]+1],
                    positions[3*indices[3*i  ]+2], 1);
            Vector4f point2 = new Vector4f(
                    positions[3*indices[3*i+1]],
                    positions[3*indices[3*i+1]+1],
                    positions[3*indices[3*i+1]+2], 1);
            Vector4f point3 = new Vector4f(
                    positions[3*indices[3*i+2]],
                    positions[3*indices[3*i+2]+1],
                    positions[3*indices[3*i+2]+2], 1); //I think I did this right

            //transform that triangle to the screen coordinates
            point1.mulProject(MVP);
            point2.mulProject(MVP); //transform the points
            point3.mulProject(MVP);
            //if the triangle isn't behind the camera and it touches the point, return true.
            if(point1.z < 1.0f && point2.z < 1.0f && point3.z < 1.0f && isInside(point1.x, point1.y, point2.x, point2.y, point3.x, point3.y, x, y)) {
                return true;
            }
        }
        //if the point touches none of the triangles, return false.
         return false;
    }


    //thanks to https://www.tutorialspoint.com/Check-whether-a-given-point-lies-inside-a-Triangle for the following code:
    //I adapted it slightly to fit my code better.

    private static double triangleArea(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return Math.abs((p1x*(p2y-p3y) + p2x*(p3y-p1y)+ p3x*(p1y-p2y))/2.0);
    }

    private static boolean isInside(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float x, float y) {
        double area = triangleArea (p1x, p1y, p2x, p2y, p3x, p3y) + .0000177;          ///area of triangle ABC //with a tiny bit of extra to avoid issues related to float precision errors
        double area1 = triangleArea (x, y, p2x, p2y, p3x, p3y);         ///area of PBC
        double area2 = triangleArea (p1x, p1y, x, y, p3x, p3y);         ///area of APC
        double area3 = triangleArea (p1x, p1y, p2x, p2y, x, y);        ///area of ABP

        return (area >= area1 + area2 + area3);        ///when three triangles are forming the whole triangle
        //I changed it to >= because floats cannot be trusted to hold perfectly accurate data,
    }
}