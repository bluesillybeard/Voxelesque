import engine.gl33.GL33Render;
import engine.multiplatform.Render;
import engine.multiplatform.model.CPUModel;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class TestGame2 {

    private static final double MOUSE_SENSITIVITY = 1;
    private static final Vector3f cameraInc = new Vector3f();
    private static final Vector3f cameraPosition = new Vector3f();
    private static final Vector3f cameraRotation = new Vector3f();

    public static void main(String[] args) {
        Render render = new GL33Render();
        if(!render.init(800, 600, args[0], true, System.err, System.err, System.out, (float)Math.toRadians(80))){
            System.err.println("Unable to initialize render");
            System.exit(-1);
        }

        CPUModel grassBlock = render.loadBlockModel("VMFModels/grassBlock.vbmf0");
        CPUModel stoneBlock = render.loadBlockModel("VMFModels/stoneBlock.vbmf0");
        int gpuGrassBlock = render.loadGPUModel(grassBlock);
        int gpuStoneBlock = render.loadGPUModel(stoneBlock);
        int defaultShader = render.loadShaderProgram("Shaders/", "");
        render.createEntity(gpuGrassBlock, defaultShader, 0, 0, 0, 0, 0, 0, 1, 1, 1);
        render.createEntity(gpuStoneBlock, defaultShader, 0, 2, 0, 0, 0, 0, 1, 1, 1);

        double lastStepTime = 0.0;
        double lastMouseYPos = render.getMouseYPos();
        double lastMouseXPos = render.getMouseXPos();
        render.setCameraPos(cameraPosition.x, cameraPosition.y, cameraPosition.z, cameraRotation.x, cameraRotation.y, cameraRotation.z);
        do{

            if(render.getTime() - lastStepTime > 1.0/30.0) {//30 times per second
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
                double CAMERA_POS_STEP = 1/3d;
                // Update camera position
                if ( cameraInc.z != 0 ) {
                    cameraPosition.x += (float)Math.sin(cameraRotation.y) * -1.0f * cameraInc.z * CAMERA_POS_STEP;
                    cameraPosition.z += (float)Math.cos(cameraRotation.y) * cameraInc.z * CAMERA_POS_STEP;
                }
                if ( cameraInc.x != 0) {
                    cameraPosition.x += (float)Math.sin(cameraRotation.y - 1.57) * -1.0f * cameraInc.x * CAMERA_POS_STEP;
                    cameraPosition.z += (float)Math.cos(cameraRotation.y - 1.57) * cameraInc.x * CAMERA_POS_STEP;
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
                render.render();

        }while(!render.shouldClose());
    }
}
