package oldEngine.render;

import java.io.IOException;

@SuppressWarnings("unused")
public interface Render {
    int WINDOW_INIT_ERROR = 1;
    int SHADER_INIT_ERROR = 2;
    int TEXTURE_INIT_ERROR = 3;
    int VEMF_LOAD_ERROR = 4;
    int TEXTURE_LOAD_ERROR = 5;
    int TEXTURE_ATLAS_ERROR = 6;
    /**
     * the first method called by the game. It should initialize any engine components, as well as create and show the window.
     * @param resourcesPath the path to the resources folder - an absolute path that is added in front of every path specified in future methods.
     * @return true if it was successful, false if it was unsuccessful.
     */
    boolean init(String title, String resourcesPath);

    /**
     * loads a shader pair within shaders. each shader pair is in the shaders directory, and it is two files:
     *     [shader]Vertex.glsl and [shader]Fragment.glsl
     * each entity has its own shader.
     * @param shader the shader pair to be loaded
     * @return the shader's ID - this is used for future methods that require a shader. returns -1 if the loading failed somehow - see String getErrors()
     */
    int loadShader(String shader);

    /**
     * loads an image file (guarantee .png, it would be nice that if you are creating a Render that you also support other formats as well)
     * Before use as a texture, it must first be converted to a texture using the addTexture() method.
     * @param image the path of the image, within the resources directory.
     * @return the image's ID - this is used for future methods that require an image. returns -1 of the loading failed somehow - see String getErrors()
     */
    int loadImage(String image);

    /**
     * unloads an image to free memory
     * @param image the image to be unloaded.
     */
    void unloadImage(int image);

    /**
     * this function is called if init(), loadShader(), or loadImage() return false / -1
     * The result is then printed to the console, or if the first 4 characters read "fatal" then it will throw an exception and crash the game.
     * @return the error string.
     */
    String getErrors();

    /**
     * @return the error code for the latest error.
     */
    int getErrorCode();
    //now start the methods that add / edit elements that are part of rendering.

    /**
     * sets the camera position
     */
    void setCameraPos(float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation);

    /**
     * creates a mesh - this is simply the frame of a model.
     * @param positions the OpenGL positions of the mesh.
     * @param textureCoordinates the texture coordinates, AKA UV
     * @param indices the indices - aka vertices of the mesh.
     * @return the ID of the mesh - this is used in methods that require a mesh
     */
    int addMesh(float[] positions, float[] textureCoordinates, int[] indices);

    /**
     * removes a mesh from the engine, clearing space. This is done automatically when closing the Render
     * @param mesh the ID of the mesh to remove.
     */
    void removeMesh(int mesh);

    /**
     * loads a VEMF model
     * @param modelPath the path to the VEMF model file
     * @return the ID of the model - used in future methods that require an entity model.
     */
    int loadVEMFModel(String modelPath);

    /**
     * Deletes a VEMF model. Note that the texture, array buffers, etc, will also be destroyed, so make sure that there are no entities using this model.
     * @param model the model to be obliterated.
     */
    void removeVEMFModel(int model);

    /**
     * adds a renderable entity to the render - the entities are the in-game objects that are rendered.
     * they contain a Mesh, Texture, Shader, and a 9 component vector for the position.
     * @param mesh the mesh of that entity.
     * @param texture the texture of that entity
     * @param shader the shader of that entity - yes, entities get their own shader.
     * @return the ID of the entity - used for methods that require an entity.
     */
    int addEntity(int mesh, int texture, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale);

    /**
     * see addEntity(int int int float[])
     * @param model The VEMF model to be used in this entity, rather than a Mesh and texture.
     * @param shader the shader of that entity
     * @return the ID of the entity - used for methods that require an entity.
     */
    int addEntity(int model, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale);


    /**
     * @param blockModel The block model to be used in this entity
     * @return the ID of the entity - used for methods that require an entity.
     */
    int addEntity(int blockModel, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale);

    /**
     * removes an entity from the Render, meaning it will no longer be rendered. Note that the Mesh, Texture, and Shader
     * are not deleted, as they are separate objects.
     */
    void removeEntity(int entity);

    //methods to modify entity data

    /**
     *
     * @param entity the entity whose position shall be set
     */
    void setEntityPosition(int entity, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale);

    /**
     * sets the model for an entity.
     * @param entity the ID of the entity whose model shall be changed
     * @param model the ID of the model the entity shall now use
     */
    void setEntityModel(int entity, int model);

    /**
     * sets the model of an entity.
     * @param entity the ID of the entity whose model shall be changed
     * @param mesh the ID of the mesh that the entity model shall use
     * @param texture the ID of the texture that the entity model should use
     */
    void setEntityModel(int entity, int mesh, int texture);
    /**
     * sets the shader of an entity.
     * @param entity the entity whose shader is to be set
     * @param shader the shader the entity is to use.
     */
    void setEntityShader(int entity, int shader);

    /**
     * converts an image into a texture
     * @param image the image from loadImage()
     * @return the ID of the texture, to be used in methods that require a texture.
     */
    int addTexture(int image);
    //methods related to text

    /**
     *
     * @param text the text to generate the model from
     * @param texture the texture atlas to use - look at resources/Textures/ASCII-Extended for example texture
     * @param shader the shader to use for the entity
     * @return the entity ID of the text - use in any method that requires an entity or TextureEntity
     */
    int addEntityFromText(String text, int texture, int shader, float XPos, float YPos, float ZPos, float XRotation, float YRotation, float ZRotation, float XScale, float YScale, float ZScale);

    //methods related to blocks

    /**
     * adds a block mesh
     * @param positions the positions of the block mesh
     * @param textureCoordinates the texture coordinates / UV coordinates
     * @param indices the indices of the mesh
     * @return the blockMesh ID
     */
    int addBlockMesh(float[] positions, float[] textureCoordinates, int[] indices);

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
    int addBlockMesh(float[] positions, float[] textureCoordinates, int[] indices, byte[] removableTriangles, byte blockedFaces);

    /**
     * loads a VBMF file into a blockMesh
     * @param VBMFPath the path of the .vbmf0 file, within the /resources folder.
     * @return the BLockMesh ID. use in methods that require a BlockMesh.
     */
    int addBlockMesh(String VBMFPath) throws IOException;

    /**
     * copies a block meshes data into a new one.
     * @param blockMesh the block mesh to be copied
     * @return the ID of the new block mesh
     */
    int copyBlockMesh(int blockMesh);

    /**
     * removes a blockMesh - If this blockMesh was generated from an entity mesh, the entity mesh won't be destroyed.
     * @param blockMesh the blockMesh to be deleted.
     */
    void removeBlockMesh(int blockMesh);

    /**
     * generates a texture atlas, and a blockModel for each of the blockMeshes.
     * There is no addBlockModel method because that is supposed to be done by this method.
     * The textures and blockMeshes with the same indexes map together.
     *
     * @param images the list of textures to use. They will be combined into one texture then sent to the GPU.
     * @param blockMeshes the list of blockMeshes. Their UV/texture coordinates will be modified to use the atlased texture.
     * @param shader the shader that all the blockModels will use.
     * @return a list of blockModel IDs, in the same order as the textures and blockMeshes.
     */
    int[] generateBlockAtlas(int[] images, int[] blockMeshes, int shader);

    //methods for Chunks

    /**
     * creates a chunk at the chunk position [x, y, z]
     * @param size how big the chunk is in each dimension
     * @param blockData a 3D array of blockModel IDs that represent that chunk's block data.
     * @return the ID of the new chunk.
     */
    int addChunk(int size, int[][][] blockData, int x, int y, int z);

    /**
     * sets the block data of a chunk.
     * @param blockData a 3D array of blockModel IDs that represent that chunk's block data.
     * @param chunk the chunk whose data will be set.
     */
    void setChunkData(int chunk, int[][][] blockData);

    /**
     * sets a specific block [z, y, x] of a chunk.
     * @param chunk the chunk whose block will be modified
     * @param block the blockModel to be used
     */
    void setChunkBlock(int chunk, int block, int x, int y, int z);
    /**
     * removes a chunk from memory so it is no longer rendered
     * @param chunk the ID of the chunk to remove
     */
    void removeChunk(int chunk);

    int getNumChunks();

    int getNumChunkSlots();
    //methods for input (many of these will be depreciated once I get the custom-controls input system complete)
    /**
     * tells weather an entity collides with a coordinate on screen.
     * Useful for seeing if the cursor interacts with GUI,
     * or interacting with the environment.
     * @param entity the entity to test
     * @param yPos the Y position to test
     * @param xPos the X position to test
     * @param usesCamera weather to use the camera transforms. False for GUI, true for 3D elements.
     * @return weather that item shows on that screen coordinate
     */
    boolean entityContacts(int entity, float yPos, float xPos, boolean usesCamera);

    /**
     * This takes a bit of explanation...
     * When a key is pressed it calls a callback.
     * That callback changes the value of that key to 2.
     * there is another one for when a key is released, which sets it to 0
     * When this function is called, the key's value is returned, then the key's value is changed based on these rules:
     *        2, 3->3
     *        0, 1->1
     * essentially, 0 means just released, 1 means released, 2 means just pushed, 3 means pushed.
     * @param key the key that you are asking information about. uses the same key codes as in GLFW, whatever those are.
     * @return they key's value - returns 0, 1, 2, or 3.
     */
    int getKey(int key);

    /**
     * similar to getKey, except for mouse buttons.
     * @param button the button to be checked
     * @return the value of the button; see getKey for more info.
     */
    int getMouseButton(int button);

    /**
     * @return the X position of the cursor on screen, [-1, 1], -1=bottom, 1=top
     */
    double getMouseXPos();

    /**
     * @return the X position of the cursor on screen, [-1, 1], -1 = left, 1=right
     */
    double getMouseYPos();

    /**
     * @return a timestamp, in seconds. As long as it counts upwards in seconds, it works.
     */
    double getTime();

    /**
     * @return true if the render method should be called, false otherwise.
     */
    boolean shouldRender();

    /**
     * renders a frame.
     */
    void render();

    /**
     * @return true if the window should be closed, false otherwise.
     */
    boolean shouldClose();

    /**
     * clears out everything related to the Render.
     * Entities, Meshes, Shaders, Textures, Window, Threads, memory allocations, etc should be cleared out once upon calling this method.
     */
    void close();

    /**
     * sets the field of view (FOV)
     * @param fov the FOV, in radians.
     */
    void setFov(float fov);

    /**
     *
     * @return the number of renderable entities
     */
    int getNumEntities();

    /**
     *
     * @return the number of entity slots - this is related to the maximum number of entities that have existed at one time.
     */
    int getNumEntitySlots();
}
