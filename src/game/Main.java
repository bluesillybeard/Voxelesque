package game;

import engine.gl33.GL33Render;
import engine.multiplatform.Render;
import engine.multiplatform.model.CPUMesh;
import game.misc.StaticUtils;
import game.world.Chunk;
import game.world.World;
import game.world.block.Block;
import game.world.block.SimpleBlock;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.TreeMap;

import static game.GlobalBits.*;
import static org.lwjgl.glfw.GLFW.*;
//todo: Main class is awful, fix this atrocity.
public class Main {

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
            tempV3f0 = new Vector3f();
            tempV3f1 = new Vector3f();
            tempV3i0 = new Vector3i();
            playerPosition = new Vector3f(0, 72, 0);
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
            double placementDistance = 5;
            render.lockMousePos();
            boolean locked = true;
            do {
                if(render.getKey(GLFW_KEY_R) == 2) world.reset();
                double worldTime = world.updateChunks(1/60.);


                updateCameraPos();
                double time = render.render();
                Runtime runtime = Runtime.getRuntime();
                Vector3i blockPos = StaticUtils.getBlockPos(playerPosition);

                if(render.getKey(GLFW_KEY_C) == 0){
                    if(locked){
                        render.unlockMousePos();
                        locked = false;
                    }
                    else {
                        render.lockMousePos();
                        locked = true;
                    }
                }

                //"raycast" to find the blocks the player might interact with
                //Actually uses a more advanced and flexible system, at the cost of performance and my sanity.
                TreeMap<Double, Vector3i> blocks = new TreeMap<>(); //K=distance from player, V=block position
                Chunk chunk = world.getChunks().get(StaticUtils.getChunkPos(playerPosition));
                Matrix4f temp = new Matrix4f();

                if(chunk != null) {
                    for (int x = 0; x < World.CHUNK_SIZE; ++x) {
                        for (int y = 0; y < World.CHUNK_SIZE; ++y) {
                            for (int z = 0; z < World.CHUNK_SIZE; ++z) {
                                CPUMesh blockMesh = chunk.getBlock(x, y, z).getMesh();

                                if (blockMesh != null && render.meshOnScreen(
                                        blockMesh,
                                        Render.getBlockTransform(temp, chunk.getPos().x*World.CHUNK_SIZE+x, chunk.getPos().y*World.CHUNK_SIZE+y, chunk.getPos().z*World.CHUNK_SIZE+z, World.CHUNK_SIZE),
                                        render.getCameraViewMatrix(), render.getCameraProjectionMatrix(), 0.5f, 0.5f
                                )) {
                                    chunk.setBlock(x, y, z, Block.VOID_BLOCK);
                                }
                            }
                        }
                    }
                }



                render.setTextEntityText(debugTextEntity,
                        "Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + " / " + runtime.totalMemory() / 1048576 +
                                "\nEntities: " + render.getNumEntities() + " / " + render.getNumEntitySlots() +
                                "\nRC:: " + render.getNumChunks() + " / " + render.getNumChunkSlots() + "|GC: " + world.getChunks().size() +
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

    private static void updateCameraPos() {
        cameraInc.set(0, 0, 0);
        if (render.getKey(GLFW_KEY_W) >= 2) {
            cameraInc.z = -1;
        } else if (render.getKey(GLFW_KEY_S) >= 2) {
            cameraInc.z = 1;
        }
        if (render.getKey(GLFW_KEY_A) >= 2) {
            cameraInc.x = -1;
        } else if (render.getKey(GLFW_KEY_D) >= 2) {
            cameraInc.x = 1;
        }
        if (render.getKey(GLFW_KEY_Z) >= 2) {
            cameraInc.y = -1;
        } else if (render.getKey(GLFW_KEY_X) >= 2) {
            cameraInc.y = 1;
        }
        // Update camera position
        double CAMERA_POS_STEP = 1 / 3d;
        if(render.getKey(GLFW_KEY_LEFT_CONTROL) >= 2) CAMERA_POS_STEP = 1.;
        if (cameraInc.z != 0) {
            playerPosition.x += (float) Math.sin(playerRotation.y) * -1.0f * cameraInc.z * CAMERA_POS_STEP;
            playerPosition.z += (float) Math.cos(playerRotation.y) * cameraInc.z * CAMERA_POS_STEP;
        }
        if (cameraInc.x != 0) {
            playerPosition.x += (float) Math.sin(playerRotation.y - 1.57) * -1.0f * cameraInc.x * CAMERA_POS_STEP;
            playerPosition.z += (float) Math.cos(playerRotation.y - 1.57) * cameraInc.x * CAMERA_POS_STEP;
        }
        playerPosition.y += cameraInc.y * CAMERA_POS_STEP;

        // Update camera based on mouse


        playerRotation.x += (render.getMouseYPos()) * sensitivity;
        playerRotation.y += (render.getMouseXPos()) * sensitivity;

        //send the camera position to Render
        render.setCameraPos(playerPosition.x, playerPosition.y, playerPosition.z, playerRotation.x, playerRotation.y, playerRotation.z);
    }
}
