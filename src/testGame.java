import engine.LWJGLRenderer;
import engine.Render;
import org.joml.AxisAngle4d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class testGame {
    private static final double MOUSE_SENSITIVITY = 100;
    private static final Vector3f cameraInc = new Vector3f();
    private static final Vector3f cameraPosition = new Vector3f();
    private static final Vector3f cameraRotation = new Vector3f();

    public static void main(String[] args){
        Render render = new LWJGLRenderer();
        if(!render.init("A test of Voxelesque engine")){
            System.err.println(render.getErrors());
            System.exit(-1);
        }
        render.setFov((float)Math.toRadians(90));
        //load "assets"
        int grassBlockTexture = render.loadImage("/home/bluesillybeard/Pictures/grassblock.png");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        int lenaTexture = render.loadImage("/home/bluesillybeard/Pictures/Lenna for generic tests of whatever.bmp");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        //you might be scrolling for a while.
        // Pretty soon i'll implement loading VEMF (Voxelesque Entity Model Format)
        // so this eyesore can be gone.
        int blockMesh = render.addMesh(
        new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,
                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,
                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,},
        new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,
                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,
                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,},
        new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,});
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        int normalShader = render.loadShader("/home/bluesillybeard/IdeaProjects/Voxelesque/src/engine/");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        int crazyShader = render.loadShader("/home/bluesillybeard/IdeaProjects/Voxelesque/src/engine/silly");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        //remember Fragment.glsl and Vertex.glsl is added to the end of this to create the final path
        int entity1 = render.addEntity(blockMesh, grassBlockTexture, normalShader, new float[]{0f, 0f, -2f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f});
        int entity2 = render.addEntity(blockMesh, grassBlockTexture, normalShader, new float[]{0.5f, 0.5f, -2f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f});
        int entity3 = render.addEntity(blockMesh, lenaTexture, normalShader, new float[]{0f, 0f, -2.5f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f});
        int entity4 = render.addEntity(blockMesh, lenaTexture, crazyShader, new float[]{0.5f, 0f, -2.5f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f});

        double lastStepTime = 0.0;
        double lastMouseYPos = render.getMouseYPos();
        double lastMouseXPos = render.getMouseXPos();
        do{
            if(render.shouldRender()){
                render.render();
            }
            if(render.getTime() - lastStepTime > 0.033333){//30 times per second
                boolean cameraUpdated = false;
                cameraInc.set(0, 0, 0);
                if (render.getKey(GLFW_KEY_W) >= 2) {
                    cameraInc.z = -1;
                    cameraUpdated = true;
                } else if (render.getKey(GLFW_KEY_S) >= 2) {
                    cameraInc.z = 1;
                    cameraUpdated = true;
                }
                if (render.getKey(GLFW_KEY_A) >= 2) {
                    cameraInc.x = -1;
                    cameraUpdated = true;
                } else if (render.getKey(GLFW_KEY_D) >= 2) {
                    cameraInc.x = 1;
                    cameraUpdated = true;
                }
                if (render.getKey(GLFW_KEY_Z) >= 2) {
                    cameraInc.y = -1;
                    cameraUpdated = true;
                } else if (render.getKey(GLFW_KEY_X) >= 2) {
                    cameraInc.y = 1;
                    cameraUpdated = true;
                }
                double CAMERA_POS_STEP = 1/20d;
                // Update camera position
                if ( cameraInc.z != 0 ) {
                    cameraPosition.x += (float)Math.sin(Math.toRadians(cameraRotation.y)) * -1.0f * cameraInc.z * CAMERA_POS_STEP;
                    cameraPosition.z += (float)Math.cos(Math.toRadians(cameraRotation.y)) * cameraInc.z * CAMERA_POS_STEP;
                }
                if ( cameraInc.x != 0) {
                    cameraPosition.x += (float)Math.sin(Math.toRadians(cameraRotation.y - 90)) * -1.0f * cameraInc.x * CAMERA_POS_STEP;
                    cameraPosition.z += (float)Math.cos(Math.toRadians(cameraRotation.y - 90)) * cameraInc.x * CAMERA_POS_STEP;
                }
                cameraPosition.y += cameraInc.y * CAMERA_POS_STEP;

                // Update camera based on mouse

                if (render.getMouseButton(GLFW_MOUSE_BUTTON_RIGHT) >= 2) {
                    cameraRotation.x += (render.getMouseYPos() - lastMouseYPos) * MOUSE_SENSITIVITY;
                    cameraRotation.y += (render.getMouseXPos() - lastMouseXPos) * MOUSE_SENSITIVITY;
                    cameraUpdated = true;
                }
                lastMouseYPos = render.getMouseYPos();
                lastMouseXPos = render.getMouseXPos();
                //send the camera position to Render
                if(cameraUpdated){
                    render.setCameraPos(cameraPosition.x, cameraPosition.y, cameraPosition.z, cameraRotation.x, cameraRotation.y, cameraRotation.z);
                }
            }
            try {
                Thread.sleep(10); //this is to keep this thread from eating up 100% after I implement multithreading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(!render.shouldClose());

        render.close();
    }
}
