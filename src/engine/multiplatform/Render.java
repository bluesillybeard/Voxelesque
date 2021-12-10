package engine.multiplatform;

import engine.multiplatform.gpu.*;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;
import org.joml.Matrix4f;

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.List;


/**
 *
 * a Render is something that converts a rendering API (OpenGL 3.3, Vulcan, etc)
 * and converts it into a more generic OOP form, which follows this interface.
 *
 * It also handles the window, and any inputs related to it.
 * Key Presses, Window Resizing, Cursor Movements, etc. should all be handled by the Render.
 *
 * Only one Render should ever exist at once.
 * if more than one Render object exist at a given moment, then someone failed their job and should be fired immediately.
 */
@SuppressWarnings("unused")
public interface Render {

    /**
     * Initializes the Render and anything contained within it.
     *
     * @param width         the width of the window (800 if given an invalid width)
     * @param height        the height of the window (600 if given an invalid height)
     * @param resourcesPath The path to the resources folder. This path is added at the front of any path to be loaded by the Render.
     * @param VSync         Vsync
     * @param warning       the warning PrintStream, Any warning will be sent through it.
     * @param error         the error PrintStream. Any errors will be sent through it.
     * @param debug         the debug PrintStream. Any debug messages will be sent through it.
     * @param fov           the field of view in radians
     * @return false if something went wrong, true if all is good.
     */
    boolean init(String title, int width, int height, String resourcesPath, boolean VSync, PrintStream warning, PrintStream error, PrintStream debug, float fov, double targetFrameTime);

    void close();
    //settings

    void setResourcesPath(String path);

    void setVSync(boolean sync);

    void setWarning(PrintStream warning);

    void setError(PrintStream error);

    void setDebug(PrintStream debug);

    void setFov(float fov);

    int getWindowHeight();

    int getWindowWidth();

    /**
     * changes the width and height of the desktop window
     * @param width the new width for the window
     * @param height the new height for the window
     * @return true if it succeeded, false if the window cannot be resized for some reason.
     */
    boolean setWindowSize(int width, int height);


    //images and textures

    /**
     * Reads an image from within the resources directory.
     *
     * @param path the path from the resources directory to read
     * @return the image - note that it must be turned into a GPU texture before it can be used for rendering.
     */
    BufferedImage readImage(String path);


    /**
     * sends a CPU-stored image into a GPU texture for rendering.
     * @param image the image to texturize
     * @return the texture refference. Use in methods that require a texture.
     */
    GPUTexture readTexture(BufferedImage image);

    /**
     * removes a texture from the GPU to free GPU memory.
     * @param texture the reference to the texture to remove
     * @return true if the texture was successfully deleted, false if something went wrong
     */
    boolean deleteTexture(GPUTexture texture);

    /**
     * combines textures into an atlas and transforms the texture coordinates of the meshes to use the atlas,
     * then generates a corresponding list of CPUModels, each having a transformed mesh and texture atlas.
     * The indices of each list correspond to the others:
     * index n of the images and index n of the meshes will end up in index n of the output list.
     *
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * @param images the input images
     * @param meshes the input meshes - they won't be modified, instead copies will be made and the copies modified.
     * @return the output list of models that all use the same texture.
     */
    CPUModel[] generateImageAtlas(BufferedImage[] images, CPUMesh[] meshes);


    /**
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * combines all the textures of the models into a single atlas, and modifies the texture coordinates of each model to use that atlas.
     * The original models are not modified, as they are copied and the copies are modified.
     * @param models the models to create the atlas.
     * @return the output list of models that all use the same texture.
     */
    List<CPUModel> generateImageAtlas(List<CPUModel> models);


    /**
     * combines textures into an atlas and transforms the texture coordinates of the meshes to use the atlas,
     * then generates a corresponding list of CPUModels, each having a transformed mesh and texture atlas.
     * The indices of each list correspond to the others:
     * index n of the images and index n of the meshes will end up in index n of the output list.
     *
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * @param images the input images
     * @param meshes the input meshes - they won't be modified, instead copies will be made and the copies modified.
     * @return the output list of models that all use the same texture.
     */
    List<CPUModel> generateImageAtlas(List<BufferedImage> images, List<CPUMesh> meshes);


    /**
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * combines all the textures of the models into a single atlas, and modifies the texture coordinates of each model to use that atlas.
     * The original models are not modified, as they are copied and the copies are modified.
     * @param models the models to create the atlas.
     * @return the output list of models that all use the same texture.
     */
    CPUModel[] generateImageAtlas(CPUModel[] models);
    //meshes
    //IMPORTANT: there are no generate CPU mesh methods - that is because CPUMesh has those in constructor form.

    /**
     * loads a CPUMesh from a .VEMF0 file.
     * Note that this can also load a VBMF file, but the block-specific data won't be loaded into the mesh.
     *  Useful for when a block and entity share the same model, which I doubt will ever happen.
     * @param VEMFPath the path within the resources folder to load
     * @return the CPUMesh defined by the file.
     */
    CPUMesh loadEntityMesh(String VEMFPath);

    /**
     * loads a CPUMesh from a .VBMF0 file.
     * a block model cannot be loaded from a VEMF like an entity can a VBMF.
     * @param VBMFPath the path of the model to load form within the resources directory
     * @return the CPUMesh loaded from the file.
     */
    CPUMesh loadBlockMesh(String VBMFPath);

    /**
     * generates a mesh that can be used for rendering.
     *
     * @param mesh The source mesh
     * @return the reference to the GPU mesh.
     */
    GPUMesh loadGPUMesh(CPUMesh mesh);

    void deleteGPUMesh(GPUMesh mesh);

    //models
    //IMPORTANT: similar to models, only methods not provided by the CPUModel constructor are provided.

    /**
     * loads a CPUModel from a .VEMF0 file.
     * Note that this can also load a VBMF file, but the block-specific data won't be loaded into the model.
     *  Useful for when a block and entity share the same model, which I doubt will ever happen.
     * @param VEMFPath the path within the resources folder to load
     * @return the CPUModel defined by the file.
     */
    CPUModel loadEntityModel(String VEMFPath);

    /**
     * loads a CPUModel from a .VBMF0 file.
     * a block model cannot be loaded from a VEMF like an entity can a VBMF.
     * @param VBMFPath the path of the model to load form within the resources directory
     * @return the CPUModel loaded from the file.
     */
    CPUModel loadBlockModel(String VBMFPath);

    /**
     * sends a CPUModel to the GPU so it can be rendered.
     * @param model the model to make renderable
     * @return a reference to the model.
     */
    GPUModel loadGPUModel(CPUModel model);

    /**
     * creates a model that can be rendered from an image and a CPUMesh
     * @param image the image
     * @param mesh the mesh
     * @return a reference to the model
     */
    GPUModel loadGPUModel(BufferedImage image, CPUMesh mesh);

    /**
     * combines a texture and mesh that has already been sent to the GPU and turns them into a model.
     * @param texture the texture for the model
     * @param mesh the mesh.
     * @return ta reference to the resulting model.
     */
    GPUModel loadGPUModel(GPUTexture texture, GPUMesh mesh);

    /**
     * deletes a model from the GPU
     * Note that the internal texture and mesh are deleted as well, so be careful.
     * @param model the GPU model to delete
     */
    void deleteGPUModel(GPUModel model);

    //shaders

    /**
     * loads a shader program and returns its ID.
     * The shader file system is a bit complicated, as this engine is designed with multiple APIs in mind.
     * possible shader folders:
     * gl33: OpenGL 3.3 shaders (GLSL)
     * gl4? OpenGL 4.? shaders (GLSL) (not implemented)
     * vc?: Vulcan ? shaders (not implemented)
     * dx?: DirectX ? (not implemented)
     * @param path the path to the shaders. The final path is: [resources]/[path]/[shader folder(gl33, dx9)]/[shader].[API shader language name(GLSL, HLSL)]
     * @param shader the shader name.
     * @return the reference to the shader.
     */
    GPUShader loadShaderProgram(String path, String shader);

    void deleteShaderProgram(GPUShader shaderProgram);

    //entities

    GPUEntity createEntity(GPUModel model, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);

    GPUEntity createEntity(GPUTexture texture, GPUMesh mesh, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);

    void setEntityPos(GPUEntity entity, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);

    void setEntityPos(GPUEntity entity, float xPos, float yPos, float zPos);

    void setEntityRotation(GPUEntity entity, float xRotation, float yRotation, float zRotation);

    void setEntityScale(GPUEntity entity, float xScale, float yScale, float zScale);

    void setEntityShader(GPUEntity entity, GPUShader shader);

    Matrix4f getEntityTransform(GPUEntity entity);

    void deleteEntity(GPUEntity entity);

    int getNumEntities();

    int getNumEntitySlots();

    //text

    /**
     * creates an entity that displays text.
     * @return the text entity ID.
     */
    GPUTextEntity createTextEntity(GPUTexture texture, String text, boolean centerX, boolean centerY, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);

    void setTextEntityPos(GPUTextEntity entity, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale);

    void setTextEntityPos(GPUTextEntity entity, float xPos, float yPos, float zPos);

    void setTextEntityRotation(GPUTextEntity entity, float xRotation, float yRotation, float zRotation);

    void setTextEntityScale(GPUTextEntity entity, float xScale, float yScale, float zScale);

    void setTextEntityShader(GPUTextEntity entity, GPUShader shader);

    void setTextEntityText(GPUTextEntity entity, String text, boolean centerX, boolean centerY);

    Matrix4f getTextEntityTransform(GPUTextEntity entity);

    void deleteTextEntity(GPUTextEntity entity);

    int getNumTextEntities();

    int getNumTextEntitySlots();

    //chunks

    /**
     * creates a chunk at the chunk position [x, y, z]
     *
     * @param size   how big the chunk is in each dimension
     * @param blocks a 3D array of CPUMeshes that represent that chunk's block data.
     * @param x the X position of the chunk
     * @param y the Y position of the chunk
     * @param z the Z position of the chunk
     * @return the ID of the new chunk.
     */
    GPUChunk spawnChunk(int size, GPUBlock[][][] blocks, int x, int y, int z, boolean buildImmediately);

    /**
     * sets the block data of a chunk.
     * @param blocks a 3D array of blockModel IDs that represent that chunk's block data.
     * @param chunk the chunk whose data will be set.
     */
    void setChunkData(GPUChunk chunk, GPUBlock[][][] blocks, boolean buildImmediately);

    /**
     * sets a specific block [z, y, x] of a chunk.
     * @param chunk the chunk whose block will be modified
     * @param block the blockModel to be used
     */
    void setChunkBlock(GPUChunk chunk, GPUBlock block, int x, int y, int z, boolean buildImmediately);

    /**
     * deletes a chunk so it is no longer rendered.
     * @param chunk the ID of the chunk to remove
     */
    void deleteChunk(GPUChunk chunk);

    int getNumChunks();

    int getNumChunkSlots();

    /**
     * completely resets and rebuilds every chunk, removing any ghost blocks.
     */
    void rebuildChunks();
    //camera
    void setCameraPos(float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation);

    Matrix4f getCameraViewMatrix();

    Matrix4f getCameraProjectionMatrix();


    //input & sensing

    void lockMousePos();

    void unlockMousePos();


    /**
     * creates a text box that the user can type into.
     * Note that a text box is simply a text entity whose text can be modified by the user,
     * which itself is just an entity that displays text.
     *
     * Although a Render it mainly a graphics API conversion, it is also an input handler
     * which is why text boxes, sliders, etc. are implemented on the Render side and not the game side.
     * @return the text box object. Pass into methods that require a text box.
     */
    GPUTextBox createTextBox();

    String getTextBoxText(GPUTextBox text);

    GPUTextBox getSelectedTextBox();

    boolean textBoxSelected(GPUTextBox text);

    void deleteTextBox(GPUTextEntity entity);

    int getNumTextBoxes();

    /**
     * Tells weather a mesh would appear on a part of the screen if it were to be rendered.
     *
     * @param mesh the mesh to transform - will not be modified as this method creates its own copy.
     * @param meshTransform the transform that moves the mesh in worldspace - input null to skip this transform
     * @param viewMatrix the transform that moves the mesh around the camera - input null to skip this transform
     * @param projectionMatrix the transform that deforms the mesh to the camera projection - input null to skip this transform
     * @param x the x position on the screen to test collision
     * @param y the y position on the screen to test collision
     * @return weather the transformed mesh would appear on the x and y coordinates.
     */
    boolean meshOnScreen(CPUMesh mesh, Matrix4f meshTransform, Matrix4f viewMatrix, Matrix4f projectionMatrix, float x, float y);



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
     * @return a timestamp, in seconds. Simply counts upwards indefinitely, not to be used to get the actual system time.
     */
    double getTime();

    boolean shouldClose();

    //other bits

    /**
     * @return true if the render method should / can be called, false otherwise.
     */
    boolean shouldRender();

    /**
     * renders a frame and collects inputs
     * @return the time it took to render the frame in seconds.
     */
    double render();


    static Matrix4f getBlockTransform(Matrix4f dest, int cx, int cy, int cz, int bx, int by, int bz, int chunkSize){
        //pretty simple stuff... That is, compared to the other crazy things I've had to do with this project.
        return dest
                .translate((cx * chunkSize+bx) * 0.288675134595f, (cy * chunkSize+by) * 0.5f , (cz * chunkSize+bz) * 0.5f)
                //*after* scaling, the mesh is translated to the right position
                .scale(0.5f ,  0.5f, ((bx + bz) & 1) - 0.5f);
                //*before* translating, the mesh is scaled to 1/2 size,
                // and mirrored on the Z axis if the sum of blockX + blockZ is an odd number
                //I don't know how I figured out the odd number thing, because I did it like a year ago and forgot.
    }

    static Matrix4f getBlockTransform(Matrix4f dest, int x, int y, int z, int chunkSize){
        int cx = (x & -chunkSize)/chunkSize;
        int cy = (y & -chunkSize)/chunkSize;
        int cz = (z & -chunkSize)/chunkSize;
        //' & -chunkSize' avoids strange issues with integer division and negative numbers.

        int bx = x&(chunkSize-1);
        int by = y&(chunkSize-1);
        int bz = z&(chunkSize-1);
        return getBlockTransform(dest, cx, cy, cz, bx, by, bz, chunkSize);

    }
}
