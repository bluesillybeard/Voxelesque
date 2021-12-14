package game;

import engine.gl33.GL33Render;
import engine.multiplatform.Render;
import engine.multiplatform.gpu.GPUTextEntity;
import engine.multiplatform.model.CPUMesh;
import game.misc.StaticUtils;
import game.misc.command.Command;
import game.misc.command.Commands;
import game.world.World;
import game.world.block.Block;
import game.world.block.SimpleBlock;
import org.joml.*;

import java.lang.Math;
import java.lang.Runtime;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import static game.GlobalBits.*;
import static org.lwjgl.glfw.GLFW.*;
//todo: Main class is awful, fix this atrocity.
public class Main {

    private static final Vector3f cameraInc = new Vector3f(0, 0, 0);

    public static void main(String[] args) {
        try {
            render = new GL33Render();
            if (!render.init("Voxelesque Alpha 0-0-0", 800, 600, "", true, System.err, System.err, System.out, (float) Math.toRadians(90), 1 / 60.)) {
                System.err.println("Unable to initialize Voxelesque engine");
                System.exit(-1);
            }
            resourcesPath = System.getProperty("user.dir") + "/resources";
            render.setResourcesPath(GlobalBits.resourcesPath);
            renderDistance = 150f;
            tempV3f0 = new Vector3f();
            tempV3f1 = new Vector3f();
            tempV3i0 = new Vector3i();
            playerPosition = new Vector3f(0, 0, 0);
            playerRotation = new Vector3f(0, 0, 0);
            sensitivity = 1;
            defaultShader = render.loadShaderProgram("Shaders/", "");
            guiShader = render.loadShaderProgram("Shaders/", "gui");
            blocks = SimpleBlock.generateBlocks(GlobalBits.resourcesPath, "BlockRegistry/voxelesque/blocks.yaml", "voxelesque");
            assert blocks != null;
            System.out.println(blocks.keySet());
            guiScale = 0.02f;
            World world = new World();
            GPUTextEntity debugTextEntity = render.createTextEntity(render.readTexture(render.readImage("Textures/ASCII-Extended.png")), "", false, false, guiShader, -1f, 1f - guiScale, 0f, 0f, 0f, 0f, guiScale, guiScale, 0f);
            double placementDistance = 5;
            render.lockMousePos();
            boolean locked = true;

            commands = new Commands();
            commands.add(Command.basicCommand("reload", world::reset));

            do {
                if (render.getKey(GLFW_KEY_T) == 2) world.reset();
                if(render.getKey(GLFW_KEY_R) == 2) render.rebuildChunks();
                double worldTime = world.updateChunks(1 / 60.);


                updateCameraPos();
                double time = render.render();
                Runtime runtime = Runtime.getRuntime();
                Vector3i blockPos = StaticUtils.getBlockPos(playerPosition);

                if (render.getKey(GLFW_KEY_C) == 2) {
                    if (locked) {
                        render.unlockMousePos();
                        locked = false;
                    } else {
                        render.lockMousePos();
                        locked = true;
                    }
                }

                if(render.getKey(GLFW_KEY_F) == 0){
                    Scanner scan = new Scanner(System.in);
                    if(scan.hasNextLine())System.out.println(commands.runCommand(world, scan.nextLine()));
                    scan.close();
                }

                //"raycast" to find the blocks the player might interact with
                //Actually uses a more advanced and flexible system, at the cost of performance and my sanity.
                TreeMap<Float, Vector3i> collidedBlocks = new TreeMap<>(); //K=distance from player, V=block position, efficiently sorts the collisions in the right order.


                Matrix4f temp = new Matrix4f();
                CPUMesh blockMesh = blocks.get("voxelesque:stoneBlock").getMesh();
                int xOff = (int) (placementDistance/0.288675134595-0.5);
                int yzOff = (int) (placementDistance*2-0.5);
                for (int x = blockPos.x - xOff; x < blockPos.x + xOff; ++x) {
                    for (int y = blockPos.y - yzOff; y < blockPos.y + yzOff; ++y) {
                        for (int z = blockPos.z - yzOff; z < blockPos.z + yzOff; ++z) {
                            temp = Render.getBlockTransform(temp.identity(), x, y, z, World.CHUNK_SIZE);

                            Vector3i pos = new Vector3i(x, y, z);
                            if (render.meshOnScreen(
                                    blockMesh,
                                    temp,
                                    render.getCameraViewMatrix(), render.getCameraProjectionMatrix(), (float) render.getMouseXPos(), (float) render.getMouseYPos()
                            )) {
                                collidedBlocks.put(
                                        new Vector3f(x * 0.288675134595f, y * 0.5f, z * 0.5f).distance(playerPosition), pos);
                            }
                        }
                    }
                }
                Set<Map.Entry<Float, Vector3i>> collidedBlocksSet = collidedBlocks.entrySet();
                Vector3i replaceable = null; //first void block followed by a non-void block in the array - this is where a block would be placed
                Vector3i breakable = null; //first non-void block in the array - this is where a block would be broken
                Vector3i last = null;
                for (Map.Entry<Float, Vector3i> o : collidedBlocksSet) {
                    Vector3i val = o.getValue();
                    Block block = world.getBlock(val);
                    if (block != null && block.getMesh() != null) {
                        //if the block isn't void
                        replaceable = last;
                        breakable = val;
                        break;
                    }
                    last = val;

                }

                if (breakable != null && render.getMouseButton(GLFW_MOUSE_BUTTON_LEFT) == 2)
                    world.getBlock(breakable).destroy(breakable, world);
                if (replaceable != null && render.getMouseButton(GLFW_MOUSE_BUTTON_RIGHT) == 2)
                    blocks.get("voxelesque:pineLeaves").place(replaceable, world);


                render.setTextEntityText(debugTextEntity,
                        "Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + " / " + runtime.totalMemory() / 1048576 +
                                "\nEntities: " + render.getNumEntities() + " / " + render.getNumEntitySlots() +
                                "\nRC:: " + render.getNumChunks() + " / " + render.getNumChunkSlots() + "|GC: " + world.getChunks().size() +
                                "\npos: " + StaticUtils.betterVectorToString(playerPosition, 3) + ", rot: (" + StaticUtils.FloatToStringSigFigs(playerRotation.x, 3) + ", " + StaticUtils.FloatToStringSigFigs(playerRotation.y, 3) + ")" +
                                "\nchunkPos: " + StaticUtils.getChunkPos(playerPosition) +
                                "\nblock: " + world.getBlock(blockPos.x, blockPos.y, blockPos.z) +
                                "\nframe: " + StaticUtils.FloatToStringSigFigs((float) (time), 10) +
                                "\nworld: " + StaticUtils.FloatToStringSigFigs((float) worldTime, 10),
                        false, false);
            } while (!render.shouldClose());
            render.close();
        } catch (Exception e) {
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
        double CAMERA_POS_STEP = 1 / 6d;
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
