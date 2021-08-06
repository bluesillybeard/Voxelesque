import engine.LWJGLRenderer;
import engine.Render;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class testGame {
    private static final double MOUSE_SENSITIVITY = 100;
    private static final Vector3f cameraInc = new Vector3f();
    private static final Vector3f cameraPosition = new Vector3f();
    private static final Vector3f cameraRotation = new Vector3f();

    private static final ArrayList<Integer> spawnedEntities = new ArrayList<>();

    public static void main(String[] args){
        Render render = new LWJGLRenderer();
        if(!render.init("A test of Voxelesque engine")){
            System.err.println(render.getErrors());
            System.exit(-1);
        }
        render.setFov((float)Math.toRadians(90));
        //load "assets"
        int grassBlockModel = render.loadVEMFModel("/home/bluesillybeard/IdeaProjects/Voxelesque/src/test2.vemf0");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        int normalShader = render.loadShader("/home/bluesillybeard/IdeaProjects/Voxelesque/src/engine/");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        int crazyShader = render.loadShader("/home/bluesillybeard/IdeaProjects/Voxelesque/src/engine/silly");
        System.err.println(render.getErrors()); //i'm too lazy to add an if statement lol
        //remember Fragment.glsl and Vertex.glsl is added to the end of this to create the final path
        int entity1 = render.addEntity(grassBlockModel, normalShader, new float[]{0f, 0f, -0f, 0f,  1f, 0.5f, 0.5f, 1.0f, 0.5f});
        int entity2 = render.addEntity(grassBlockModel, normalShader, new float[]{0f, 0f, -2f, 1f,  1f, 0f,   1.0f, 0.5f, 0.5f});
        int entity3 = render.addEntity(grassBlockModel, normalShader, new float[]{0f, 0f, -4f, 0f,  0f, 1f,   0.5f, 0.5f, 1.0f});
        int entity4 = render.addEntity(grassBlockModel, crazyShader, new float[] {0f, 0f, -6f, 0f,  2f, 0f,   0.5f, 0.5f, 0.5f});

        double lastStepTime = 0.0;
        double lastFramerateDebugTime = 0.0;
        double lastMouseYPos = render.getMouseYPos();
        double lastMouseXPos = render.getMouseXPos();
        int frames = 0;
        do{
            if(render.shouldRender()){
                render.render();
                frames++;
                if(render.getKey(GLFW_KEY_F) == 3){
                    spawnedEntities.add(render.addEntity(grassBlockModel, normalShader, new float[]{cameraPosition.x, cameraPosition.y-1, cameraPosition.z, cameraRotation.x, cameraRotation.z, cameraRotation.y, 1.0f, 1.0f, 1.0f}));
                }
            }
            if(render.getTime() - lastStepTime > 0.033333){//30 times per second
                lastStepTime = render.getTime();
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

                render.setEntityPosition(entity1, new float[]{0f, 0f, -0f, (float)(render.getTime()/10),  (float)(render.getTime()*5), (float)render.getTime(), 0.5f, 1.0f, 0.5f});
                render.setEntityPosition(entity2, new float[]{0f, 0f, -2f, (float)(render.getTime()*9),  (float)(render.getTime()/7), (float)render.getTime()*1.5f,   1.0f, 0.5f, 0.5f});
                render.setEntityPosition(entity3, new float[]{0f, 0f, -4f, (float)(render.getTime()/3),  (float)(render.getTime()*2), (float)render.getTime()/0.5f,   0.5f, 0.5f, 1.0f});
            }
            if(render.getTime() - lastFramerateDebugTime > 1.0){
                lastFramerateDebugTime = render.getTime();
                System.out.print("rendering " + spawnedEntities.size() + " entities. ");
                System.out.println("framerate:" + frames);
                frames = 0;
            }
            //try {
            //    Thread.sleep(1); //this is to keep this thread from eating up 100% after I implement multithreading
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
        }while(!render.shouldClose());

        render.close();
    }
}
