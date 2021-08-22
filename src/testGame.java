import engine.render.LWJGLRenderer;
import engine.render.Render;
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

    private static final Runtime jre = Runtime.getRuntime();

    public static void main(String[] args){
        Render render = new LWJGLRenderer();
        if(!render.init("A test of Voxelesque engine", "/home/bluesillybeard/IdeaProjects/Voxelesque/resources/")){
            System.err.println(render.getErrors());
            System.exit(-1);
        }
        render.setFov((float)Math.toRadians(90));
        //shaders
        int normalShader = render.loadShader(""); //default shaders
        int crazyShader = render.loadShader("silly");
        int guiShader = render.loadShader("gui");

        int blockMesh = render.addBlockMesh(new float[]{
                //(0.57735026919,-0.5),(-0.57735026919, -0.5),(0.0, 0.5)
                0.57735026919f, 1.0f, -0.5f, //top face positions for side faces (texture coordinates)
                -0.57735026919f, 1.0f, -0.5f,
                0.0f, 1.0f, 0.5f,

                0.57735026919f, 0.0f, -0.5f, //bottom face positions for side faces (texture coordinates)
                -0.57735026919f, 0.0f, -0.5f,
                0.0f, 0.0f, 0.5f,

                0.57735026919f, 1.0f, -0.5f, //top face
                -0.57735026919f, 1.0f, -0.5f,
                0.0f, 1.0f, 0.5f,

                0.57735026919f, 0.0f, -0.5f, //bottom face
                -0.57735026919f, 0.0f, -0.5f,
                0.0f, 0.0f, 0.5f,
        }, new float[]{
                0.0f, 0.3f,
                1.0f, 0.3f,
                0.5f, 0.3f,

                0.0f, 0.7f,
                1.0f, 0.7f,
                0.5f, 0.7f,

                0.0f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,

                0.0f, 0.6f,
                1.0f, 0.6f,
                0.5f, 1.0f,
        }, new int[]{
                6, 7, 8, //top face
                9, 10, 11, //bottom face

                //side faces:
                0, 1, 3,
                1, 3, 4,

                1, 2, 4,
                2, 4, 5,

                2, 0, 3,
                2, 5, 3,
        });

        int stoneBlockMesh = render.copyBlockMesh(blockMesh);

        int guiMesh1 = render.addMesh(new float[]{
                -1, -1, 0,
                1, -1, 0,
                -1, 1, 0,
                1, 1, 0,
        },
                new float[]{
                        0, 1,
                        1, 1,
                        0, 0,
                        1, 0,
                },
                new int[]{
                        0, 1, 2,
                        1, 2, 3,
                });
        //images
        int grassImage = render.loadImage("Textures/grass.png");
        int stoneImage = render.loadImage("Textures/stone.png");
        int happyImage = render.loadImage("Textures/happy.png");
        int sadImage = render.loadImage("Textures/sad.png");
        int textImage = render.loadImage("Textures/ASCII.png");

        //textures
        int textTexture = render.addTexture(textImage);
        int happyTexture = render.addTexture(happyImage);
        int sadTexture = render.addTexture(sadImage);
        //Models
        int grassBlockModel = render.loadVEMFModel("VMFModels/test2.vemf0");
        int[] atlasModels = render.generateBlockAtlas(new int[]{grassImage, stoneImage}, new int[]{blockMesh, stoneBlockMesh}, normalShader);
        //Entities
        int guiEntity1 = render.addEntity(guiMesh1, happyTexture, guiShader, -0.8f, -0.8f, 0.0f,  0, 0, 0,  0.2f, 0.2f, 0.0f);

        int entity1 = render.addEntity(grassBlockModel, normalShader, 0f, 10f,  0f, 0f,  1f, 0.5f, 0.5f, 1.0f, 0.5f);
        int entity2 = render.addEntity(grassBlockModel, normalShader, 0f, 10f, -2f, 1f,  1f, 0f,   1.0f, 0.5f, 0.5f);
        int entity3 = render.addEntity(grassBlockModel, normalShader, 0f, 10f, -4f, 0f,  0f, 1f,   0.5f, 0.5f, 1.0f);
        int entity4 = render.addEntity(grassBlockModel, crazyShader,  0f, 10f, -6f, 0f,  2f, 0f,   0.5f, 0.5f, 0.5f);

        int textEntity = render.addEntityFromText("I just rewrote the entire \ntext rendering algorithm just to add new lines!" +
                "\n Seriously, it was surprisingly difficult, \nbut at least it is much more flexible now! :D", textTexture, normalShader,10, 0, 0, 0, 1, 0, 0.25f, 0.25f, 0.25f);
        //Chunks
        int grassBlock = atlasModels[0];
        int stoneBlock = atlasModels[1];
        int airBlock = -1; //when a chunk is rendered, -1 is treated as void, aka nothing renders in that spot.
        render.addChunk(4, new int[][][]{
                {
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {grassBlock, grassBlock, grassBlock, grassBlock},
                        {airBlock, airBlock, airBlock, airBlock},
                },
                {
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {grassBlock, stoneBlock, grassBlock, grassBlock},
                        {airBlock, grassBlock, airBlock, airBlock},
                },
                {
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {grassBlock, grassBlock, grassBlock, grassBlock},
                        {airBlock, airBlock, airBlock, airBlock},
                },
                {
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {stoneBlock, stoneBlock, stoneBlock, stoneBlock},
                        {grassBlock, grassBlock, grassBlock, grassBlock},
                        {airBlock, airBlock, airBlock, airBlock},
                },
        }, 0, 0, 0);


        double lastStepTime = 0.0;
        double lastFramerateDebugTime = 0.0;
        double lastMouseYPos = render.getMouseYPos();
        double lastMouseXPos = render.getMouseXPos();
        int frames = 0;
        do{
            if(render.getTime() - lastStepTime > 0.033333){//30 times per second
                lastStepTime = render.getTime();
                if(render.entityContacts(guiEntity1, (float)render.getMouseYPos(), (float)render.getMouseXPos(), false)){
                    render.setEntityModel(guiEntity1, guiMesh1, sadTexture);
                } else {
                    render.setEntityModel(guiEntity1, guiMesh1, happyTexture);
                }
                if(render.getKey(GLFW_KEY_F) >= 2){
                    spawnedEntities.add(render.addEntity(grassBlockModel, normalShader, cameraPosition.x, cameraPosition.y-1, cameraPosition.z, 0, 0, 0, 1.0f, 1.0f, 1.0f));
                }
                for(int i=0; i<spawnedEntities.size(); i++){
                    if(spawnedEntities.get(i) != -1 && (render.entityContacts(spawnedEntities.get(i), (float)render.getMouseYPos(), (float)render.getMouseXPos(), true) && render.getMouseButton(GLFW_MOUSE_BUTTON_LEFT) >= 2)) {
                        render.removeEntity(spawnedEntities.get(i)); //remove each entity
                        spawnedEntities.set(i, -1);
                    }

                }


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
                double CAMERA_POS_STEP = 1/10d;
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

                render.setEntityPosition(entity1, 0f, 10f, -0f, (float)(render.getTime()/10),  (float)(render.getTime()*5), (float)render.getTime(), 0.5f, 1.0f, 0.5f);
                render.setEntityPosition(entity2, 0f, 10f, -2f, (float)(render.getTime()*9),  (float)(render.getTime()/7), (float)render.getTime()*1.5f,   1.0f, 0.5f, 0.5f);
                render.setEntityPosition(entity3, 0f, 10f, -4f, (float)(render.getTime()/3),  (float)(render.getTime()*2), (float)render.getTime()/0.5f,   0.5f, 0.5f, 1.0f);
            }
            if(render.getTime() - lastFramerateDebugTime > 1.0){
                lastFramerateDebugTime = render.getTime();
                System.out.print("Entities: " + render.getNumEntities());
                System.out.print(" | Chunks: " + render.getNumChunks());
                System.out.print(" | framerate:" + frames);
                System.out.print(" | free memory: " + (jre.freeMemory()/1048576) +  "mb");

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

        render.close();
    }
}
