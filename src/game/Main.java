package game;

import engine.gl33.GL33Render;
import game.misc.StaticUtils;
import game.world.World;
import game.world.block.SimpleBlock;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static game.GlobalBits.*;
import static org.lwjgl.glfw.GLFW.*;
//todo: Main class is awful, fix this atrocity.
public class Main {
    private static double lastMouseYPos = 0;
    private static double lastMouseXPos = 0;

    private static final Vector3f cameraInc = new Vector3f(0, 0, 0);

    public static void main(String[] args) {
        try {
            render = new GL33Render();
            if (!render.init("Voxelesque Alpha 0-0-0", 800, 600, "", true, System.err, System.err, System.out, (float) Math.toRadians(90), 1/60.)) {
                System.err.println("Unable to initialize Voxelesque engine");
                System.exit(-1);
            }
            resourcesPath = System.getProperty("user.dir") + "/resources";
            render.setResourcesPath(GlobalBits.resourcesPath);
            renderDistance = 150f;
            tempV3f = new Vector3f();
            playerPosition = new Vector3f(36, 72, 25);
            playerRotation = new Vector3f(0, 0, 0);
            sensitivity = 1;
            defaultShader = render.loadShaderProgram("Shaders/", "");
            guiShader = render.loadShaderProgram("Shaders/", "gui");
            blocks = SimpleBlock.generateBlocks(GlobalBits.resourcesPath, "BlockRegistry/voxelesque/blocks.yaml", "voxelesque");
            assert blocks != null;
            System.out.println(blocks.keySet());
            guiScale = 0.03f;
            World world = new World();
            int debugTextEntity = render.createTextEntity(render.readTexture(render.readImage("Textures/ASCII-Extended.png")), "", false, false, guiShader, -1f, 1f - guiScale, 0f, 0f, 0f, 0f, guiScale, guiScale, 0f);
            do {
                double worldTime = world.updateChunks(1/60.);


                updateCameraPos();
                double time = render.render();
                Runtime runtime = Runtime.getRuntime();
                Vector3i blockPos = StaticUtils.getBlockPos(playerPosition);

                if(render.getKey(GLFW_KEY_P) == 2){
                    world.setBlock(blockPos.x, blockPos.y, blockPos.z, blocks.get("voxelesque:stoneBlock"));
                }

                render.setTextEntityText(debugTextEntity,
                        "Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + " / " + runtime.totalMemory() / 1048576 +
                                "\nEntities: " + render.getNumEntities() + " / " + render.getNumEntitySlots() +
                                "\nChunks: " + render.getNumChunks() + " / " + render.getNumChunkSlots() +
                                "\npos: " + StaticUtils.betterVectorToString(playerPosition, 3) + ", rot: (" + StaticUtils.FloatToStringSigFigs(playerRotation.x, 3) + ", " + StaticUtils.FloatToStringSigFigs(playerRotation.y, 3) + ")" +
                                "\nchunkPos: " + StaticUtils.getChunkPos(playerPosition) +
                                "\nblock: " + world.getBlock(blockPos.x, blockPos.y, blockPos.z) +
                                "\nframe: " + StaticUtils.FloatToStringSigFigs((float)(time), 10) +
                                "\nworld: " + StaticUtils.FloatToStringSigFigs((float)worldTime, 10),
                        false, false);
            } while (!render.shouldClose());
            render.close();
        } catch(Exception e){
            e.printStackTrace();
            render.close();
        }
    }

    private static void updateCameraPos(){
        cameraInc.set(0, 0, 0);
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
        // Update camera position
        double CAMERA_POS_STEP = 1/3d;
        if ( cameraInc.z != 0 ) {
            playerPosition.x += (float)Math.sin(playerRotation.y) * -1.0f * cameraInc.z * CAMERA_POS_STEP;
            playerPosition.z += (float)Math.cos(playerRotation.y) * cameraInc.z * CAMERA_POS_STEP;
        }
        if ( cameraInc.x != 0) {
            playerPosition.x += (float)Math.sin(playerRotation.y - 1.57) * -1.0f * cameraInc.x * CAMERA_POS_STEP;
            playerPosition.z += (float)Math.cos(playerRotation.y - 1.57) * cameraInc.x * CAMERA_POS_STEP;
        }
        playerPosition.y += cameraInc.y * CAMERA_POS_STEP;

        // Update camera based on mouse

        if (render.getMouseButton(GLFW_MOUSE_BUTTON_RIGHT) >= 2) {
            playerRotation.x += (render.getMouseYPos() - lastMouseYPos) * sensitivity;
            playerRotation.y += (render.getMouseXPos() - lastMouseXPos) * sensitivity;
            cameraUpdated = true;
        }
        lastMouseYPos = render.getMouseYPos();
        lastMouseXPos = render.getMouseXPos();
        //send the camera position to Render
        if(cameraUpdated){
            render.setCameraPos(playerPosition.x, playerPosition.y, playerPosition.z, playerRotation.x, playerRotation.y, playerRotation.z);
        }
    }
}
