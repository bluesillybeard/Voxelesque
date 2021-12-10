import engine.gl33.GL33Render;
import engine.multiplatform.Render;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class TestGame2 {

    private static final double MOUSE_SENSITIVITY = 1;
    private static final Vector3f cameraInc = new Vector3f();
    private static final Vector3f cameraPosition = new Vector3f();
    private static final Vector3f cameraRotation = new Vector3f();

    private static final Runtime jre = Runtime.getRuntime();

    private static final ArrayList<Integer> spawnedEntities = new ArrayList<>();

    public static void main(String[] args) {
        Render render = new GL33Render();
        if(!render.init("Voxelesque Engine Test Game", 800, 600, args[0], true, System.err, System.err, System.out, (float)Math.toRadians(80))){
            System.err.println("Unable to initialize render");
            System.exit(-1);
        }

        CPUModel grassVoxel = render.loadBlockModel("VMFModels/grassBlock.vbmf0");
        CPUModel stoneVoxel = render.loadBlockModel("VMFModels/stoneBlock.vbmf0");

        CPUModel[] voxels = render.generateImageAtlas(new CPUModel[]{grassVoxel, stoneVoxel});

        int gpuChunkTexture = render.readTexture(voxels[0].texture);
        int gpuGrassVoxel = render.loadGPUModel(grassVoxel);
        int gpuStoneVoxel = render.loadGPUModel(stoneVoxel);
        int gpuGrassVoxelAtlas = render.loadGPUModel(voxels[0]);
        int gpuStoneVoxelAtlas = render.loadGPUModel(voxels[1]);

        CPUModel grassBlock = render.loadEntityModel("VMFModels/test2.vemf0");
        int gpuGrassBlock = render.loadGPUModel(grassBlock);



        int defaultShader = render.loadShaderProgram("Shaders/", "");
        int sillyShader = render.loadShaderProgram("Shaders/", "silly");

        int grassEntity = render.createEntity(gpuGrassVoxel, defaultShader, -5f, 0f,  0f, 0f,  0f, 0f, 1f, 1f, 1f);
        int stoneEntity = render.createEntity(gpuStoneVoxel, defaultShader, -5f, 1f,  0f, 0f,  0f, 0f, 1f, 1f, 1f);
        int grassEntityAtlas = render.createEntity(gpuGrassVoxelAtlas, defaultShader, -5f, 2f,  0f, 0f,  0f, 0f, 1f, 1f, 1f);
        int stoneEntityAtlas = render.createEntity(gpuStoneVoxelAtlas, defaultShader, -5f, 3f,  0f, 0f,  0f, 0f, 1f, 1f, 1f);


        int entity1 = render.createEntity(gpuGrassBlock, defaultShader, 0f, 10f,  0f, 0f,  1f, 0.5f, 0.5f, 1.0f, 0.5f);
        int entity2 = render.createEntity(gpuGrassBlock, defaultShader, 0f, 10f, -2f, 1f,  1f, 0f,   1.0f, 0.5f, 0.5f);
        int entity3 = render.createEntity(gpuGrassBlock, defaultShader, 0f, 10f, -4f, 0f,  0f, 1f,   0.5f, 0.5f, 1.0f);
        int entity4 = render.createEntity(gpuGrassBlock, sillyShader,  0f, 10f, -6f, 0f,  2f, 0f,   0.5f, 0.5f, 0.5f);
        int entity5 = render.createEntity(gpuGrassBlock, defaultShader, 0, 0, 64, 0, 0, 0, 10, 10, 10);


        CPUMesh[][][] randomChunkModels = new CPUMesh[64][64][64];
        int[][][] randomChunkTextures = new int[64][64][64];
        int[][][] randomChunkShaders = new int[64][64][64];
        for(int x = 0; x < randomChunkModels.length; x++){
            for(int y=0; y<randomChunkModels[x].length; y++){
                for(int z=0; z<randomChunkModels[x][y].length; z++){
                    int index = (int)(Math.random()*3-1);
                    randomChunkModels[x][y][z] = voxels[index].mesh;
                    randomChunkTextures[x][y][z] = gpuChunkTexture;
                    randomChunkShaders[x][y][z] = defaultShader;

                }
            }
        }
        int renderDistance = 6;
        for(int x=0; x<renderDistance; x++){
            for(int y=0;y<renderDistance/2;y++){
                for(int z=0;z<renderDistance/2;z++){
                    render.spawnChunk(64, randomChunkModels, randomChunkTextures, randomChunkShaders, x, y, z);
                    System.out.println("created chunk " + x + ", " + y + ", " + z + ";" + render.getNumChunks());
                }
            }
        }


        double lastStepTime = 0.0;
        double lastFramerateDebugTime = 0.0;
        double lastMouseYPos = render.getMouseYPos();
        double lastMouseXPos = render.getMouseXPos();
        int frames = 0;
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


                render.setEntityPos(entity1, 0f, 10f, -0f, (float)(render.getTime()/10),  (float)(render.getTime()*5), (float)render.getTime(), 0.5f, 1.0f, 0.5f);
                render.setEntityPos(entity2, 0f, 10f, -2f, (float)(render.getTime()*9),  (float)(render.getTime()/7), (float)render.getTime()*1.5f,   1.0f, 0.5f, 0.5f);
                render.setEntityPos(entity3, 0f, 10f, -4f, (float)(render.getTime()/3),  (float)(render.getTime()*2), (float)render.getTime()/0.5f,   0.5f, 0.5f, 1.0f);


                if(render.getKey(GLFW_KEY_F) >= 2){
                    spawnedEntities.add(render.createEntity(gpuGrassBlock, defaultShader, cameraPosition.x, cameraPosition.y-1, cameraPosition.z, 0, 0, 0, 1.0f, 1.0f, 1.0f));
                }
                for(int i=0; i<spawnedEntities.size(); i++){
                    if(spawnedEntities.get(i) != -1 && render.meshOnScreen(grassBlock.mesh, render.getEntityTransform(spawnedEntities.get(i)), render.getCameraViewMatrix(), render.getCameraProjectionMatrix(), (float)render.getMouseXPos(), (float)render.getMouseYPos()) && render.getMouseButton(GLFW_MOUSE_BUTTON_LEFT) >= 2) {
                        render.deleteEntity(spawnedEntities.get(i)); //remove each entity
                        spawnedEntities.set(i, -1);
                    }

                }
            }
            if(render.getTime() - lastFramerateDebugTime > 1.0){
                lastFramerateDebugTime = render.getTime();
                System.out.print("Entities: " + render.getNumEntities());
                System.out.print(" | Chunks: " + render.getNumChunks());
                System.out.print(" | framerate:" + frames);
                System.out.print(" | memory: " + (jre.totalMemory()/1048576) + " / " + (jre.maxMemory()/1048576) + " mb used");

                System.out.print("\n");
                frames = 0;
            }
            try {
                Thread.sleep(1); //this is to keep this thread from eating up 100% after I implement multithreading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(render.shouldRender()){
                render.render();
                frames++;
            }

        }while(!render.shouldClose());
    }
}
