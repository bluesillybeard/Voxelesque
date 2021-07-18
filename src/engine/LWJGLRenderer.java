package engine;

import engine.graph.Mesh;
import engine.graph.ShaderProgram;
import engine.graph.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class LWJGLRenderer implements Render{
    private Window window;
    private String errorString;
    private int errorCode;
    private boolean readyToRender;
    private float FOV;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();

    private final ArrayList<GameItem> gameItems = new ArrayList<>(); //TODO: store these more efficiently
    private final ArrayList<ShaderProgram> shaderPrograms = new ArrayList<>();
    private final ArrayList<Texture>textures = new ArrayList<>();
    private final ArrayList<Mesh> meshes = new ArrayList<>();

    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraRotation = new Vector3f();
    /**
     * the first method called by the game. It should initialize any engine components, as well as create and show the window.
     *
     * @return true if it was successful, false if it was unsuccessful.
     */
    @Override
    public boolean init(String title) {

        try {
            window = new Window(title, 800, 600, true);
            window.init();
            readyToRender = true;
            return true; //everything went well, so return true.
        } catch(Exception e){
            errorString = Arrays.toString(e.getStackTrace());
            errorCode = WINDOW_INIT_ERROR;
            readyToRender = true;
            return false;
        }
    }



    /**
     * loads a shader pair within shaders. each shader pair is in the shaders directory, and it is two files:
     * [shader]Vertex.glsl and [shader]Fragment.glsl
     * each entity has its own shader.
     *
     * @param shader the shader pair to be loaded
     * @return the shader's ID - this is used for future methods that require a shader. returns -1 if the loading failed somehow - see String getErrors()
     */
    @Override
    public int loadShader(String shader) {
        try {
            shaderPrograms.add(new ShaderProgram(
                    Utils.loadResource(shader + "Vertex.glsl"),
                    Utils.loadResource(shader + "Fragment.glsl")
            ));
            return shaderPrograms.size()-1;
        } catch (Exception e) {
            errorString = Arrays.toString(e.getStackTrace());
            errorCode = SHADER_INIT_ERROR;
            return -1;
        }
    }

    /**
     * loads an image file (guaranteed .png, probably supports most other formats as well.)
     *
     * @param image the path of the image, within the resources directory.
     * @return the image's ID - this is used for future methods that require an image. returns -1 of the loading failed somehow - see String getErrors()
     */
    @Override
    public int loadImage(String image) {
        try {
            textures.add(new Texture(image));
            return textures.size() - 1;
        } catch(Exception e){
            errorString = Arrays.toString(e.getStackTrace());
            errorCode = TEXTURE_INIT_ERROR;
            return -1;
        }
    }

    /**
     * sets the camera position
     */
    @Override
    public void setCameraPos(float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation) {
        cameraPosition.x = XPos;
        cameraPosition.y = YPos;
        cameraPosition.z = XPos;
        cameraRotation.x = XRotation;
        cameraRotation.y = YRotation;
        cameraRotation.z = ZRotation;
    }

    /**
     * creates a mesh - this is simply the frame of a model.
     *
     * @param positions          the OpenGL positions of the mesh.
     * @param textureCoordinates the texture coordinates, AKA UV
     * @param indices            the indices - aka vertices of the mesh.
     * @return the ID of the mesh - this is used in methods that require a mesh
     */
    @Override
    public int addMesh(float[] positions, float[] textureCoordinates, int[] indices) {
        meshes.add(new Mesh(positions, textureCoordinates, indices));
        return meshes.size()-1;
    }

    /**
     * removes a mesh from the engine, clearing space. This is done automatically when closing the Render
     *
     * @param mesh the ID of the mesh to remove.
     */
    @Override
    public void removeMesh(int mesh) {
        meshes.set(mesh, null);
    }

    /**
     * adds a renderable entity to the render - the entities are the in-game objects that are rendered.
     * they contain a Mesh, Texture, Shader, and a 9 component vector for the position.
     *
     * @param mesh     the mesh of that entity.
     * @param texture  the texture of that entity
     * @param shader   the shader of that entity - yes, entities get their own shader.
     * @param position the location, rotation, and scale of the entity. [XPos, YPos, ZPos, XRotation, YRotation, ZRotation, XScale, YScale, ZScale]
     * @return the ID of the entity - used for methods that require an entity.
     *
     * NOTE: As of 0.0.0, LWJGLRenderer is not capable of 3D scaling, so they are averaged into a single scale factor.
     */
    @Override
    public int addEntity(int mesh, int texture, int shader, float[] position) {
        GameItem item = new GameItem(
                meshes.get(mesh),
                shaderPrograms.get(shader),
                textures.get(texture)
        );
        item.setPosition(position[0], position[1], position[2]);
        item.setRotation(position[3], position[4], position[5]);
        item.setScale((position[6] + position[7] + position[8])/3f); //TODO: make scale a vector rather than number.
        gameItems.add(item);
        return gameItems.size()-1;
    }

    /**
     * removes an entity from the Render, meaning it will no longer be rendered. Note that the Mesh, Texture, and Shader
     * are not deleted, as they are separate objects.
     *
     * @param entity the entity ID to be removed
     */
    @Override
    public void removeEntity(int entity) {
        gameItems.set(entity, null); //set that entity to null so it is no longer rendered.
    }

    /**
     * This takes a bit of explanation...
     * When a key is pressed it calls a callback.
     * That callback changes the value of that key to 2.
     * there is another one for when a key is released, which sets it to 0
     * When this function is called, the key's value is returned, then the key's value is changed based on these rules:
     * 2, 3->3
     * 0, 1->1
     * essentially, 0 means just released, 1 means released, 2 means just pushed, 3 means pushed.
     *
     * @param key the key that you are asking information about. uses the same key codes as in GLFW, whatever those are.
     * @return they key's value - returns 0, 1, 2, or 3.
     */
    @Override
    public int getKey(int key) {return window.getKey(key);}

    /**
     * similar to getKey, except for mouse buttons.
     *
     * @param button the button to be checked
     * @return the value of the button; see getKey for more info.
     */
    @Override
    public int getMouseButton(int button) {return window.getMouseButton(button);}

    /**
     * @return the X position of the cursor on screen, [-1, 1], -1=bottom, 1=top
     */
    @Override
    public double getMouseXPos() {
        return 0;//window.;
    }

    /**
     * @return the X position of the cursor on screen, [-1, 1], -1 = left, 1=right
     */
    @Override
    public double getMouseYPos() {
        return 0;
    }

    /**
     * @return a timestamp, in seconds. As long as it counts upwards in seconds, it works.
     */
    @Override
    public double getTime() {return System.nanoTime() / 1_000_000_000.;}

    /**
     * @return true if the render method should be called, false otherwise.
     */
    @Override
    public boolean shouldRender() {return readyToRender;}

    /**
     * renders a frame.
     */
    @Override
    public void render() {
        readyToRender = false;
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }


        // Update projection Matrix
        projectionMatrix.setPerspective(FOV, (float) window.getWidth() / window.getHeight(), 1/256f, Float.MAX_VALUE/2);
        // Update view Matrix
        // First do the rotation so camera rotates over its position
        viewMatrix.identity().rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        for(ShaderProgram shaderProgram: shaderPrograms) {
            shaderProgram.bind();

            shaderProgram.setProjectionMatrix(projectionMatrix);
            shaderProgram.setViewMatrix(viewMatrix);
            shaderProgram.setTextureSampler(0);

            shaderProgram.unbind();
        }
        // Render each gameItem
        for (GameItem gameItem : gameItems) {
            // Render the mesh for this game item
            gameItem.render();
        }
        window.update();
        readyToRender = true;
    }

    /**
     * @return true if the window should be closed, false otherwise.
     */
    @Override
    public boolean shouldClose() {return window.windowShouldClose();}

    /**
     * clears out everything related to the Render.
     * Entities, Meshes, Shaders, Textures, Window, Threads, memory allocations, etc should be cleared out once upon calling this method.
     */
    @Override
    public void close() {
        //clean up items that wouldn't be cleaned by the garbage collector; items that are held within the GPU.
        // I considered C++ for a bit because I had to do this anyway, but I decided against it because
        // C++ is whack when you're used to the simplicity of Java.
        for (Mesh mesh : meshes) {
            mesh.cleanUp();
        }
        for(ShaderProgram shaderProgram: shaderPrograms) {
            if (shaderProgram != null) {
                shaderProgram.cleanup();
            }
        }
        for(Texture texture: textures){
            texture.cleanUp();
        }
    }

    /**
     * sets the field of view (FOV)
     *
     * @param fov the FOV, in radians.
     */
    @Override
    public void setFov(float fov) {FOV = fov;}

    /**
     * this function is called if init(), loadShader(), or loadImage() return false / -1
     * The result is then printed to the console, or if the first 4 characters read "fatal" then it will throw an exception and crash the game.
     *
     * @return the error string.
     */
    @Override
    public String getErrors() {return errorString;}

    @Override
    public int getErrorCode(){return errorCode;}

    /**
     * the Major version of the Rendering engine. Major versions are completely incompatible; no intentional backwards compatibility of any kind.
     * The current latest version is 0.
     *
     * @return the major version of the Render.
     */
    @Override
    public int getVersionMajor() {return 0;}

    /**
     * each minor version should be mostly backwards compatible with older versions.
     * If the game needs version 2, then version 3, 4, 5, etc need to work as well.
     * the current latest version is 0.
     *
     * @return the minor version of the Render
     */
    @Override
    public int getVersionMinor() {return 0;}

    /**
     * returns the patch version of the render. Patch versions should only fix bugs, exploits, glitches, etc,
     * and any patch version should be 100% compatible with all other patch versions of a minor/major version.
     * For example, if the game needs patch 2, patch 5 should work as well and vice versa if possible.
     * the current latest version is 0.
     *
     * @return the patch version of the Render.
     */
    @Override
    public int getVersionPatch() {return 0;}
}
