package engine;

import engine.graph.Mesh;
import engine.graph.ShaderProgram;
import engine.graph.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Matrix4f projectionMatrix;

    private Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;

    private final Vector3f cameraInc;

    private final Vector3f cameraPosition;

    private final Vector3f cameraRotation;

    private GameItem[] gameItems;

    private static final float FOV = (float) Math.toRadians(90);

    private static final float Z_NEAR = 1/256f;

    private static final float Z_FAR = Float.MAX_VALUE/2f;

    private ShaderProgram shaderProgram;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        cameraPosition = new Vector3f(0, 0, 0);
        cameraRotation = new Vector3f(0, 0, 0);
        cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram(
                Utils.loadResource("src/engine/vertex.glsl"),
                Utils.loadResource("src/engine/fragment.glsl"));
        float[] positions = new float[]{
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
                0.5f, -0.5f, 0.5f,};
        float[] textCoords = new float[]{
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
                1.0f, 0.5f,};
        int[] indices = new int[]{
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
                4, 6, 7, 5, 4, 7,};
        Texture texture = new Texture("/home/bluesillybeard/Pictures/grassblock.png");
        Mesh mesh = new Mesh(positions, textCoords, indices, texture);
        GameItem gameItem1 = new GameItem(mesh);
        gameItem1.setScale(0.5f);
        gameItem1.setPosition(0, 0, -2);
        GameItem gameItem2 = new GameItem(mesh);
        gameItem2.setScale(0.5f);
        gameItem2.setPosition(0.5f, 0.5f, -2);
        GameItem gameItem3 = new GameItem(mesh);
        gameItem3.setScale(0.5f);
        gameItem3.setPosition(0, 0, -2.5f);
        GameItem gameItem4 = new GameItem(mesh);
        gameItem4.setScale(0.5f);
        gameItem4.setPosition(0.5f, 0, -2.5f);
        gameItems = new GameItem[]{gameItem1, gameItem2, gameItem3, gameItem4};
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(MouseInput mouseInput) {
        // Update camera position
        if ( cameraInc.z * CAMERA_POS_STEP != 0 ) {
            cameraPosition.x += (float)Math.sin(Math.toRadians(cameraRotation.y)) * -1.0f * cameraInc.z * CAMERA_POS_STEP;
            cameraPosition.z += (float)Math.cos(Math.toRadians(cameraRotation.y)) * cameraInc.z * CAMERA_POS_STEP;
        }
        if ( cameraInc.x * CAMERA_POS_STEP != 0) {
            cameraPosition.x += (float)Math.sin(Math.toRadians(cameraRotation.y - 90)) * -1.0f * cameraInc.x * CAMERA_POS_STEP;
            cameraPosition.z += (float)Math.cos(Math.toRadians(cameraRotation.y - 90)) * cameraInc.x * CAMERA_POS_STEP;
        }
        cameraPosition.y += cameraInc.y * CAMERA_POS_STEP;

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            cameraRotation.x += rotVec.x * MOUSE_SENSITIVITY;
            cameraRotation.y += rotVec.y * MOUSE_SENSITIVITY;
        }
    }

    @Override
    public void render(Window window) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        projectionMatrix.setPerspective(FOV, (float)window.getWidth() / window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setProjectionMatrix(projectionMatrix);

        // Update view Matrix
        // First do the rotation so camera rotates over its position
        viewMatrix.identity().rotate((float)Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        shaderProgram.setTextureSamplerUniform(0);
        // Render each gameItem
        for (GameItem gameItem : gameItems) {


            // Set model view matrix for this item
            Vector3f rotation = gameItem.getRotation();
            modelViewMatrix.identity().translate(gameItem.getPosition()).
                    rotateX((float)Math.toRadians(-rotation.x)).
                    rotateY((float)Math.toRadians(-rotation.y)).
                    rotateZ((float)Math.toRadians(-rotation.z)).
                    scale(gameItem.getScale());
            Matrix4f viewCurr = new Matrix4f(viewMatrix);
            modelViewMatrix = viewCurr.mul(modelViewMatrix);

            shaderProgram.setModelViewMatrix(modelViewMatrix);
            // Render the mes for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    @Override
    public void cleanup() {
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}