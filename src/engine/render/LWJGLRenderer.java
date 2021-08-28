package engine.render;

import engine.VMF.VEMFLoader;
import engine.model.*;
import engine.util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class LWJGLRenderer implements Render{
    private Window window;
    private String errorString;
    private String resourcesPath;


    private int errorCode;
    private boolean readyToRender;
    private float FOV;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();

    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraRotation = new Vector3f();

    private final SlottedArrayList<BufferedImage> images = new SlottedArrayList<>();
    private final SlottedArrayList<RenderableEntity> renderableEntities = new SlottedArrayList<>();
    private final SlottedArrayList<ShaderProgram> shaderPrograms = new SlottedArrayList<>();
    private final SlottedArrayList<Texture>textures = new SlottedArrayList<>();
    private final SlottedArrayList<Mesh> meshes = new SlottedArrayList<>();
    private final SlottedArrayList<Model> models = new SlottedArrayList<>();
    private final SlottedArrayList<BlockMesh> blockMeshes = new SlottedArrayList<>();
    private final SlottedArrayList<BlockModel> blockModels = new SlottedArrayList<>();
    private final SlottedArrayList<RenderableChunk> chunks = new SlottedArrayList<>();

    private final VEMFLoader entityLoad = new VEMFLoader();

    /**
     * the first method called by the game. It should initialize any engine components, as well as create and show the window.
     * @param resourcesPath the path to the resources folder - an absolute path that is added in front of every path specified in future methods.
     * @return true if it was successful, false if it was unsuccessful.
     */
    @Override
    public boolean init(String title, String resourcesPath) {
        try {
            this.window = new Window(title, 800, 600, true);
            this.window.init();
            this.readyToRender = true;
            this.resourcesPath = resourcesPath;
            return true; //everything went well, so return true.
        } catch(Exception e){
            this.errorString = getStackTrace(e);
            this.errorCode = WINDOW_INIT_ERROR;
            this.readyToRender = true;
            return false;
        }
    }

    private String getStackTrace(Exception e){
        StringOutputStream out = new StringOutputStream(new StringBuffer());
        e.printStackTrace(new PrintStream(out));
        return out.toString();
    }

    /**
     * loads a shader pair within shaders. each shader pair is in the GLSLShaders directory within the resources directory, and it is two files:
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
                    Utils.loadResource(resourcesPath + "GLSLShaders/" + shader + "Vertex.glsl"),
                    Utils.loadResource(resourcesPath + "GLSLShaders/" + shader + "Fragment.glsl")
            ));
            return shaderPrograms.size()-1;
        } catch (Exception e) {
            errorString = getStackTrace(e);
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
            return images.add(ImageIO.read(new File(resourcesPath + image)));
        } catch(Exception e){
            errorString = getStackTrace(e);
            errorCode = TEXTURE_LOAD_ERROR;
            return -1;
        }
    }

    /**
     * unloads an image to free memory
     *
     * @param image the image to be unloaded.
     */
    @Override
    public void unloadImage(int image) {
        images.remove(image);
    }

    /**
     * sets the camera position
     */
    @Override
    public void setCameraPos(float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation) {
        cameraPosition.x = XPos;
        cameraPosition.y = YPos;
        cameraPosition.z = ZPos;
        cameraRotation.x = XRotation;
        cameraRotation.y = YRotation;
        cameraRotation.z = ZRotation;
        // Update view Matrix
        viewMatrix.identity().rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0))
                .translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
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
        return meshes.add(new Mesh(positions, textureCoordinates, indices));
    }

    /**
     * removes a mesh from the engine, clearing space. This is done automatically when closing the Render
     *
     * @param mesh the ID of the mesh to remove.
     */
    @Override
    public void removeMesh(int mesh) {
        meshes.remove(mesh);
    }

    /**
     * loads a VEMF model
     *
     * @param modelPath the path to the VEMF model file
     * @return the ID of the model - used in future methods that require an entity model.
     */
    @Override
    public int loadVEMFModel(String modelPath) {
        try {
            return models.add(new Model(entityLoad.loadVEMF(new File(resourcesPath + modelPath))));
        } catch(IOException e){
            errorString = getStackTrace(e);
            errorCode = VEMF_LOAD_ERROR;
            return -1;
        }
    }

    /**
     * Deletes a VEMF model. Note that the texture, array buffers, etc, will also be destroyed, so make sure that there are no entities using this model.
     *
     * @param model the model to be obliterated.
     */
    @Override
    public void removeVEMFModel(int model) {
        models.get(model).cleanUp();
        models.remove(model);
    }

    /**
     * adds a renderable entity to the render - the entities are the in-game objects that are rendered.
     * they contain a Mesh, Texture, Shader, and a 9 component vector for the position.
     *
     * @param mesh     the mesh of that entity.
     * @param texture  the texture of that entity
     * @param shader   the shader of that entity - yes, entities get their own shader.
     * @return the ID of the entity - used for methods that require an entity.
     */
    @Override
    public int addEntity(int mesh, int texture, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale) {
        RenderableEntity item = new RenderableEntity(
                meshes.get(mesh),
                shaderPrograms.get(shader),
                textures.get(texture)
        );
        item.setPosition(XPos, YPos, ZPos);
        item.setRotation(XRotation, YRotation, ZRotation);
        item.setScale(XScale, YScale, ZScale);
        return renderableEntities.add(item);
    }

    /**
     * see addEntity(int int int float[])
     *
     * @param model    The VEMF model to be used in this entity, rather than a Mesh and texture.
     * @param shader   the shader of that entity
     * @return the ID of the entity - used for methods that require an entity.
     */
    @Override
    public int addEntity(int model, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale) {
        RenderableEntity item = new RenderableEntity(
                models.get(model),
                shaderPrograms.get(shader)
        );
        item.setPosition(XPos, YPos, ZPos);
        item.setRotation(XRotation, YRotation, ZRotation);
        item.setScale(XScale, YScale, ZScale);
        return renderableEntities.add(item);
    }

    /**
     * @param blockModel The block model to be used in this entity
     * @return the ID of the entity - used for methods that require an entity.
     */
    @Override
    public int addEntity(int blockModel, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale) {
        RenderableEntity entity = new RenderableEntity(new Model(blockModels.get(blockModel)), blockModels.get(blockModel).getShader());
        entity.setPosition(XPos, YPos, ZPos);
        entity.setRotation(XRotation, YRotation, ZRotation);
        entity.setScale(XScale, YScale, ZScale);
        return renderableEntities.add(entity);
    }

    /**
     * @param text      the text to generate the model from
     * @param shader    the shader to use for the entity
     * @return the entity ID of the text - use in any method that requires an entity or TextureEntity
     */
    @Override
    public int addEntityFromText(String text, int texture, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale) {
        RenderableEntity entity = TextMeshBuilder.generateEntity(text, textures.get(texture), shaderPrograms.get(shader), true, true);
        entity.setPosition(XPos, YPos, ZPos);
        entity.setRotation(XRotation, YRotation, ZRotation);
        entity.setScale(XScale, YScale, ZScale);
        return renderableEntities.add(entity);
    }

    /**
     * removes an entity from the Render, meaning it will no longer be rendered. Note that the Mesh, Texture, and Shader
     * are not deleted, as they are separate objects.
     *
     * @param entity the entity ID to be removed
     */
    @Override
    public void removeEntity(int entity) {
        renderableEntities.remove(entity);
    }

    /**
     * @param entity   the entity whose position shall be set
     */
    @Override
    public void setEntityPosition(int entity, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale) {
        RenderableEntity item = renderableEntities.get(entity);
        item.setPosition(XPos, YPos, ZPos);
        item.setRotation(XRotation, YRotation, ZRotation);
        item.setScale(XScale, YScale, ZScale);
    }

    /**
     * sets the model for an entity.
     *
     * @param entity the ID of the entity whose model shall be changed
     * @param model  the ID of the model the entity shall now use
     */
    @Override
    public void setEntityModel(int entity, int model) {
        renderableEntities.get(entity).setModel(models.get(model));
    }

    /**
     * sets the model of an entity.
     *
     * @param entity  the ID of the entity whose model shall be changed
     * @param mesh    the ID of the mesh that the entity model shall use
     * @param texture the ID of the texture that the entity model should use
     */
    @Override
    public void setEntityModel(int entity, int mesh, int texture) {
        renderableEntities.get(entity).setModel(meshes.get(mesh), textures.get(texture));
    }

    /**
     * sets the shader of an entity.
     *
     * @param entity the entity whose shader is to be set
     * @param shader the shader the entity is to use.
     */
    @Override
    public void setEntityShader(int entity, int shader) {
        renderableEntities.get(entity).setShaderProgram(shaderPrograms.get(shader));
    }

    /**
     * converts an image into a texture
     *
     * @param image the image from loadImage()
     * @return the ID of the texture, to be used in methods that require a texture.
     */
    @Override
    public int addTexture(int image) {
        return textures.add(new Texture(images.get(image)));
    }

    /**
     * adds a block mesh
     *
     * @param mesh an entity mesh, in case a block and entity have the same mesh for some reason
     * @return the blockMesh ID
     */
    @Override
    public int addBlockMesh(int mesh) {
        Mesh mesh1 = meshes.get(mesh);
        return blockMeshes.add(new BlockMesh(mesh1.getPositions(), mesh1.getUVCoords(), mesh1.getIndices()));
    }

    /**
     * adds a block mesh
     *
     * @param positions          the positions of the block mesh
     * @param textureCoordinates the texture coordinates / UV coordinates
     * @param indices            the indices of the mesh
     * @return the blockMesh ID
     */
    @Override
    public int addBlockMesh(float[] positions, float[] textureCoordinates, int[] indices) {
        return blockMeshes.add(new BlockMesh(positions, textureCoordinates, indices));
    }


    /**
     * adds a block mesh
     *
     * @param positions          the positions of the block mesh
     * @param textureCoordinates the texture coordinates / UV coordinates
     * @param indices            the indices of the mesh
     * @param removableTriangles TODO: explain
     * @param blockedFaces       the faces that this block blocks of other blocks [top (+y), bottom(-y), (-z / +z), -x, +x]
     * @return the blockMesh ID
     */
    @Override
    public int addBlockMesh(float[] positions, float[] textureCoordinates, int[] indices, int[] removableTriangles, boolean[] blockedFaces) {
        return blockMeshes.add(new BlockMesh(positions, textureCoordinates, indices, removableTriangles, blockedFaces));
    }

    /**
     * copies a block meshes data into a new one.
     *
     * @param blockMesh the block mesh to be copied
     * @return the ID of the new block mesh
     */
    @Override
    public int copyBlockMesh(int blockMesh) {
        return blockMeshes.add(blockMeshes.get(blockMesh).clone());
    }

    /**
     * removes a blockMesh - If this blockMesh was generated from an entity mesh, the entity mesh won't be destroyed.
     *
     * @param blockMesh the blockMesh to be deleted.
     */
    @Override
    public void removeBlockMesh(int blockMesh) {
        blockMeshes.remove(blockMesh);
    }

    /**
     * generates a texture atlas, and a blockModel for each of the blockMeshes.
     * There is no addBlockModel method because that is supposed to be done by this method.
     * The textures and blockMeshes with the same indexes map together.
     * note: you don't neet to make a model for blocks that are blank - use -1 as the block ID to render nothing.
     * @param images the list of textures to use. Make sure that they are local textures!
     * @param blockMeshes the list of blockMeshes
     * @return a list of blockModel IDs, in the same order as the textures and blockMeshes.
     */
    @Override
    public int[] generateBlockAtlas(int[] images, int[] blockMeshes, int shader) {
        if(images.length != blockMeshes.length){
            this.errorString = "in Render.generateBlockAtlas: localTextures and blockMeshes differ in length! \n" +
                    "Cannot link textures to meshes when there are meshes/textures that link to a nonexistent texture/mesh.";
            this.errorCode = TEXTURE_ATLAS_ERROR;
            return null;
        }
        BufferedImage[] textures = new BufferedImage[images.length];
        BlockMesh[] meshes = new BlockMesh[blockMeshes.length];
        for(int i = 0; i< images.length; i++){
            textures[i] = this.images.get(images[i]);
            meshes[i] = this.blockMeshes.get(blockMeshes[i]); //get the image and mesh objects
        }
        BlockModel[] models = AtlasGenerator.generateBlockModels(textures, meshes, this.shaderPrograms.get(shader));
        assert models != null;
        int[] returner = new int[models.length];
        for(int i = 0; i< images.length; i++){
            returner[i] = this.blockModels.add(models[i]); //place the model objects into the lists.
        }
        return returner;
    }

    /**
     * creates a chunk at the chunk position [x, y, z]
     *
     * @param blockData a 3D array of blockModel IDs that represent that chunk's block data.
     * @return the ID of the new chunk.
     */
    @Override
    public int addChunk(int size, int[][][] blockData, int x, int y, int z) {
        return chunks.add(new RenderableChunk(size, blockData, x, y, z));
    }

    /**
     * sets the block data of a chunk.
     *
     * @param chunk     the chunk whose data will be set.
     * @param blockData a 3D array of blockModel IDs that represent that chunk's block data.
     */
    @Override
    public void setChunkData(int chunk, int[][][] blockData) {
        chunks.get(chunk).setData(blockData);
    }

    /**
     * sets a specific block [z, y, x] of a chunk.
     *
     * @param chunk the chunk whose block will be modified
     * @param block the blockModel to be used
     */
    @Override
    public void setChunkBlock(int chunk, int block, int x, int y, int z) {
        chunks.get(chunk).setBlock(block, x, y, z);
    }

    /**
     * removes a chunk from memory so it is no longer rendered
     *
     * @param chunk the ID of the chunk to remove
     */
    @Override
    public void removeChunk(int chunk) {
        chunks.remove(chunk);
    }

    @Override
    public int getNumChunks() {
        return chunks.size();
    }

    @Override
    public int getNumChunkSlots() {
        return chunks.capacity();
    }

    /**
     * tells weather an entity collides with a coordinate on screen.
     * Useful for seeing if the cursor interacts with GUI,
     * or interacting with the environment.
     *
     * @param entity     the entity to test
     * @param yPos       the Y position to test
     * @param xPos       the X position to test
     * @param usesCamera weather to use the camera transforms. False for GUI, true for 3D elements.
     * @return weather that item shows on that screen coordinate
     */
    @Override
    public boolean entityContacts(int entity, float yPos, float xPos, boolean usesCamera) {
        if(usesCamera)
            return renderableEntities.get(entity).touchesPositionOnScreen(yPos, xPos, viewMatrix, projectionMatrix);
        else
            return renderableEntities.get(entity).touchesPositionOnScreen(yPos, xPos, null, null);
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
        return window.getCursorXPos();
    }

    /**
     * @return the X position of the cursor on screen, [-1, 1], -1 = left, 1=right
     */
    @Override
    public double getMouseYPos() {
        return window.getCursorYPos();
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
            window.setResized(false);
            glViewport(0, 0, window.getWidth(), window.getHeight());
            // Update projection Matrix
            projectionMatrix.setPerspective(FOV, (float) window.getWidth() / window.getHeight(), 1/256f, 8192f);
        }

        //update shader uniforms
        for(ShaderProgram shaderProgram: shaderPrograms) {
            shaderProgram.bind();

            shaderProgram.setGameTime();
            shaderProgram.setProjectionMatrix(projectionMatrix);
            shaderProgram.setViewMatrix(viewMatrix);
            shaderProgram.setTextureSampler(0);
        }
        // Render each gameItem
        for (RenderableEntity renderableEntity : renderableEntities) {
            // Render the mesh for this game item
            renderableEntity.render();
        }
        //render each chunk
        for (RenderableChunk chunk: chunks){
            if(chunk.shouldBuild)chunk.build(blockModels.toArray(new BlockModel[blockModels.size()]));
            chunk.render();
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
     * Entities, Meshes, Shaders, Textures, Window, Threads, memory allocations, etc will be cleared out once upon calling this method.
     */
    @Override
    public void close() {
        //clean up items that wouldn't be cleaned by the garbage collector; items that are held within the GPU.
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
        for(Model model: models){
            model.cleanUp();
        }
        System.out.println("LWJGLRenderer closed properly.");
    }

    /**
     * sets the field of view (FOV)
     *
     * @param fov the FOV, in radians.
     */
    @Override
    public void setFov(float fov) {
        FOV = fov;
        // Update projection Matrix
        projectionMatrix.setPerspective(FOV, (float) window.getWidth() / window.getHeight(), 1/256f, 8192f);
    }

    /**
     * @return the number of renderable entities
     */
    @Override
    public int getNumEntities() {
        return renderableEntities.size();
    }

    /**
     * @return the number of entity slots - this is related to the maximum number of entities that have existed at one time.
     */
    @Override
    public int getNumEntitySlots() {
        return renderableEntities.capacity();
    }

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
}
