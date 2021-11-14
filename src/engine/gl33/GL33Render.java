package engine.gl33;

import VMF.VMFLoader;
import engine.gl33.model.GL33Mesh;
import engine.gl33.model.GL33Model;
import engine.gl33.model.GL33Texture;
import engine.gl33.render.*;
import engine.gl33.render.Window;
import engine.multiplatform.Render;
import engine.multiplatform.Util.AtlasGenerator;
import engine.multiplatform.Util.Utils;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;
import engine.multiplatform.model.RenderBlockModel;
import engine.multiplatform.render.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class GL33Render implements Render {
    //file stuff
    private String resourcesPath;

    //internal class bits
    private final Window window;

    private final Vector3f cameraPosition;
    private final Vector3f cameraRotation;
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    private final Vector4f tempv4f1;
    private final Vector4f tempv4f2;
    private final Vector4f tempv4f3;
    private final Matrix4f tempMat;

    private final Set<GL33Shader> shaders;
    private final Set<GL33Entity> entities;
    private final Set<GL33TextEntity> textEntities;
    private final Set<GL33Chunk> chunks;

    private boolean shouldRender;
    private float fov;

    //static internal class bits
    private static final BufferedImage errorImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    private static boolean staticInitialized;
    private static final VMFLoader loader = new VMFLoader();

    //print streams
    private PrintStream errorStream;
    private PrintStream warningStream;
    private PrintStream debugStream;

    public GL33Render(){
        window = new Window();
        shaders = new HashSet<>();
        entities = new HashSet<>();
        textEntities = new HashSet<>();
        chunks = new HashSet<>();
        cameraPosition = new Vector3f();
        cameraRotation = new Vector3f();
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        tempMat = new Matrix4f();
        tempv4f1 = new Vector4f();
        tempv4f2 = new Vector4f();
        tempv4f3 = new Vector4f();
    }
    /**
     * Initializes the Render and anything contained within it.
     *
     * @param title         the title of the window.
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
    @Override
    public boolean init(String title, int width, int height, String resourcesPath, boolean VSync, PrintStream warning, PrintStream error, PrintStream debug, float fov) {
        errorStream = error;
        warningStream = warning;
        debugStream = debug;
        this.resourcesPath = resourcesPath;
        this.fov = fov;
        this.shouldRender = true;

        updateProjectionMatrix();
        updateViewMatrix();

        if(!staticInitialized){
            staticInitialized = true;
            errorImage.setRGB(0, 0, Color.BLACK.getRGB());
            errorImage.setRGB(1, 0, Color.MAGENTA.getRGB());
            errorImage.setRGB(0, 1, Color.BLACK.getRGB());
            errorImage.setRGB(1, 1, Color.MAGENTA.getRGB());
        }
        try {
            window.init(title, width, height, VSync);

            return true;
        } catch(Exception e) {
            e.printStackTrace(errorStream);
            return false;
        }
    }

    @Override
    public void close() {
        window.close();
        errorStream.flush();
    }

    @Override
    public void setResourcesPath(String path) {
        this.resourcesPath = path;
    }

    @Override
    public void setVSync(boolean sync) {
        window.setVSync(sync);
    }

    @Override
    public void setWarning(PrintStream warning) {
        warningStream = warning;
    }

    @Override
    public void setError(PrintStream error) {
        errorStream = error;
    }

    @Override
    public void setDebug(PrintStream debug) {
        debugStream = debug;
    }

    @Override
    public void setFov(float fov) {
        this.fov = fov;
        updateProjectionMatrix();
    }

    @Override
    public int getWindowHeight() {
        return window.getHeight();
    }

    @Override
    public int getWindowWidth() {
        return window.getWidth();
    }

    /**
     * changes the width and height of the desktop window
     *
     * @param width  the new width for the window
     * @param height the new height for the window
     * @return true if it succeeded, false if the window cannot be resized for some reason.
     */
    @Override
    public boolean setWindowSize(int width, int height) {
        window.setSize(width, height);
        updateProjectionMatrix();
        return true;
    }

    /**
     * Reads an image from within the resources directory.
     *
     * @param path the path from the resources directory to read
     * @return the image - note that it must be turned into a GPU texture before it can be used for rendering.
     */
    @Override
    public BufferedImage readImage(String path) {
        try {
            return ImageIO.read(new File(resourcesPath + "/" + path));
        } catch(Exception e){
            e.printStackTrace(errorStream);
            return errorImage;
        }
    }

    /**
     * sends a CPU-stored image into a GPU texture for rendering.
     *
     * @param image the image to texturize
     * @return the GPU-stored texture. Use in methods that require a texture.
     */
    @Override
    public GPUTexture readTexture(BufferedImage image) {
        return new GL33Texture(image);
    }

    /**
     * removes a texture from the GPU to free GPU memory.
     *
     * @param texture the texture to remove from the GPU
     * @return true if the texture was successfully deleted, false if something went wrong
     */
    @Override
    public boolean deleteTexture(GPUTexture texture) {
        ((GL33Texture)texture).cleanUp();
        return true;
    }

    /**
     * combines textures into an atlas and transforms the texture coordinates of the meshes to use the atlas,
     * then generates a corresponding list of CPUModels, each having a transformed mesh and texture atlas.
     * The indices of each list correspond to the others:
     * index n of the images and index n of the meshes will end up in index n of the output list.
     * <p>
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     *
     * @param images the input images
     * @param meshes the input meshes - they won't be modified, instead copies will be made and the copies modified.
     * @return the output list of models that all use the same texture.
     */
    @Override
    public CPUModel[] generateImageAtlas(BufferedImage[] images, CPUMesh[] meshes) {
        return AtlasGenerator.generateCPUModels(images, meshes, errorStream);
    }

    /**
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * combines all the textures of the models into a single atlas, and modifies the texture coordinates of each model to use that atlas.
     * The original models are not modified, as they are copied and the copies are modified.
     *
     * @param models the models to create the atlas.
     * @return the output list of models that all use the same texture.
     */
    @Override
    public List<CPUModel> generateImageAtlas(List<CPUModel> models) {
        return AtlasGenerator.generateCPUModels(models, errorStream);
    }

    /**
     * combines textures into an atlas and transforms the texture coordinates of the meshes to use the atlas,
     * then generates a corresponding list of CPUModels, each having a transformed mesh and texture atlas.
     * The indices of each list correspond to the others:
     * index n of the images and index n of the meshes will end up in index n of the output list.
     * <p>
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     *
     * @param images the input images
     * @param meshes the input meshes - they won't be modified, instead copies will be made and the copies modified.
     * @return the output list of models that all use the same texture.
     */
    @Override
    public List<CPUModel> generateImageAtlas(List<BufferedImage> images, List<CPUMesh> meshes) {
        return AtlasGenerator.generateCPUModels(images, meshes, errorStream);
    }

    /**
     * This is highly advised to use on models for blocks, as chunks need an entire draw call per texture they use.
     * combines all the textures of the models into a single atlas, and modifies the texture coordinates of each model to use that atlas.
     * The original models are not modified, as they are copied and the copies are modified.
     *
     * @param models the models to create the atlas.
     * @return the output list of models that all use the same texture.
     */
    @Override
    public CPUModel[] generateImageAtlas(CPUModel[] models) {
        return AtlasGenerator.generateCPUModels(models, errorStream);
    }

    /**
     * loads a CPUMesh from a .VEMF0 file.
     * Note that this can also load a VBMF file, but the block-specific data won't be loaded into the mesh.
     * Useful for when a block and entity share the same model, which I doubt will ever happen.
     *
     * @param VEMFPath the path within the resources folder to load
     * @return the CPUMesh defined by the file. Null if something went wrong.
     */
    @Override
    public CPUMesh loadEntityMesh(String VEMFPath) {
        try {
            return new CPUMesh(loader.loadVEMF(new File(resourcesPath + "/" + VEMFPath)));
        } catch(Exception e){
            e.printStackTrace(errorStream);
            return null;
        }
    }

    /**
     * loads a CPUMesh from a .VBMF0 file.
     * a block model cannot be loaded from a VEMF like an entity can a VBMF.
     *
     * @param VBMFPath the path of the model to load form within the resources directory
     * @return the CPUMesh loaded from the file.
     */
    @Override
    public CPUMesh loadBlockMesh(String VBMFPath) {
        try {
            return new CPUMesh(loader.loadVBMF(new File(resourcesPath + "/" + VBMFPath)));
        } catch(Exception e){
            e.printStackTrace(errorStream);
            return null;
        }
    }

    /**
     * generates a mesh that can be used for rendering.
     *
     * @param mesh The source mesh
     * @return the reference to the GPU mesh.
     */
    @Override
    public GPUMesh loadGPUMesh(CPUMesh mesh) {
        return new GL33Mesh(mesh);
    }

    /**
     * loads a CPUModel from a .VEMF0 file.
     * Note that this can also load a VBMF file, but the block-specific data won't be loaded into the model.
     * Useful for when a block and entity share the same model, which I doubt will ever happen.
     *
     * @param VEMFPath the path within the resources folder to load
     * @return the CPUModel defined by the file.
     */
    @Override
    public CPUModel loadEntityModel(String VEMFPath) {
        try {
            return new CPUModel(loader.loadVEMF(new File(resourcesPath + "/" + VEMFPath)));
        } catch(Exception e){
            e.printStackTrace(errorStream);
            return null;
        }
    }

    /**
     * loads a CPUModel from a .VBMF0 file.
     * a block model cannot be loaded from a VEMF like an entity can a VBMF.
     *
     * @param VBMFPath the path of the model to load form within the resources directory
     * @return the CPUModel loaded from the file.
     */
    @Override
    public CPUModel loadBlockModel(String VBMFPath) {
        try {
            return new CPUModel(loader.loadVBMF(new File(resourcesPath + "/" + VBMFPath)));
        } catch(Exception e){
            e.printStackTrace(errorStream);
            return null;
        }    }

    /**
     * sends a CPUModel to the GPU so it can be rendered.
     *
     * @param model the model to make renderable
     * @return a reference to the model.
     */
    @Override
    public GPUModel loadGPUModel(CPUModel model) {
        return new GL33Model(model);
    }

    /**
     * creates a model that can be rendered from an image and a CPUMesh
     *
     * @param image the image
     * @param mesh  the mesh
     * @return a reference to the model
     */
    @Override
    public GPUModel loadGPUModel(BufferedImage image, CPUMesh mesh) {
        return new GL33Model(mesh, image);
    }

    /**
     * combines a texture and mesh that has already been sent to the GPU and turns them into a model.
     *
     * @param texture the texture for the model
     * @param mesh    the mesh.
     * @return ta reference to the resulting model.
     */
    @Override
    public GPUModel loadGPUModel(GPUTexture texture, GPUMesh mesh) {
        return new GL33Model((GL33Mesh)mesh, (GL33Texture)texture);
    }

    /**
     * deletes a model from the GPU
     * Note that the internal texture and mesh are deleted as well, so be careful.
     *
     * @param model the GPU model to delete
     */
    @Override
    public void deleteGPUModel(GPUModel model) {
        GL33Model glModel = (GL33Model)model;
        glModel.mesh.cleanUp();
        glModel.texture.cleanUp();
    }

    /**
     * loads a shader program and returns its ID.
     * The shader file system is a bit complicated, as this engine is designed with multiple APIs in mind.
     * possible shader folders:
     * gl33: OpenGL 3.3 shaders (GLSL)
     * gl4? OpenGL 4.? shaders (GLSL) (not implemented)
     * vc?: Vulcan ? shaders (not implemented)
     * dx?: DirectX ? (not implemented)
     *
     * @param path   the path to the shaders. The final path is: [resources]/[path]/[shader folder(gl33, dx9)]/[shader].[API shader language name(GLSL, HLSL)]
     * @param shader the shader name.
     * @return the shader. null if it failed.
     */
    @Override
    public GPUShader loadShaderProgram(String path, String shader) {
        try {
            GL33Shader shader1 = new GL33Shader(Utils.loadResource(resourcesPath + "/" +  path + "/gl33/" + shader + "Vertex.glsl"), Utils.loadResource(resourcesPath + "/" + path + "/gl33/" + shader + "Fragment.glsl"));
            shaders.add(shader1);
            return shader1;
        } catch (Exception e) {
            e.printStackTrace(errorStream);
            return null;
        }
    }

    @Override
    public void deleteShaderProgram(GPUShader shaderProgram) {
        shaders.remove((GL33Shader)shaderProgram);
        ((GL33Shader)shaderProgram).cleanup();
    }

    @Override
    public GPUEntity createEntity(GPUModel model, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale) {

        GL33Entity entity = new GL33Entity((GL33Model)model, (GL33Shader)shader);
        entity.setPosition(xPos, yPos, zPos);
        entity.setRotation(xRotation, yRotation, zRotation);
        entity.setScale(xScale, yScale, zScale);
        entities.add(entity);
        return entity;
    }

    @Override
    public GPUEntity createEntity(GPUTexture texture, GPUMesh mesh, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale) {
        GL33Entity entity = new GL33Entity((GL33Mesh)mesh, (GL33Shader)shader, (GL33Texture)texture);
        entity.setPosition(xPos, yPos, zPos);
        entity.setRotation(xRotation, yRotation, zRotation);
        entity.setScale(xScale, yScale, zScale);
        entities.add(entity);
        return entity;
    }

    @Override
    public void setEntityPos(GPUEntity entity, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale) {
        GL33Entity entity1 = ((GL33Entity)entity);
        entity1.setPosition(xPos, yPos, zPos);
        entity1.setRotation(xRotation, yRotation, zRotation);
        entity1.setScale(xScale, yScale, zScale);
    }

    @Override
    public void setEntityPos(GPUEntity entity, float xPos, float yPos, float zPos) {
        ((GL33Entity)entity).setPosition(xPos, yPos, zPos);
    }

    @Override
    public void setEntityRotation(GPUEntity entity, float xRotation, float yRotation, float zRotation) {
        ((GL33Entity)entity).setRotation(xRotation, yRotation, zRotation);
    }

    @Override
    public void setEntityScale(GPUEntity entity, float xScale, float yScale, float zScale) {
        ((GL33Entity)entity).setScale(xScale, yScale, zScale);
    }

    @Override
    public void setEntityShader(GPUEntity entity, GPUShader shader) {
        ((GL33Entity)entity).setShaderProgram((GL33Shader)shader);
    }

    @Override
    public Matrix4f getEntityTransform(GPUEntity entity) {
        return ((GL33Entity)entity).getModelViewMatrix();
    }

    @Override
    public void deleteEntity(GPUEntity entity) {
        entities.remove((GL33Entity)entity);
    }

    @Override
    public int getNumEntities() {
        return entities.size();
    }

    @Override
    public int getNumEntitySlots() {
        return entities.size();
    }

    /**
     * creates an entity that displays text.
     *
     * @param texture the font texture; A 16x16 grid of characters that represents ASCII.
     * @param text the text to display.
     * @param centerX Weather to center the text on the X axis.
     * @param centerY Weather to center the text on the Y axis.
     * @param shader The shader to use.
     * @return the text entity ID.
     */
    @Override
    public GPUTextEntity createTextEntity(GPUTexture texture, String text, boolean centerX, boolean centerY, GPUShader shader, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale) {
        GL33TextEntity entity = new GL33TextEntity(text, (GL33Shader)shader, (GL33Texture)texture, centerX, centerY);
        entity.setPosition(xPos, yPos, zPos);
        entity.setRotation(xRotation, yRotation, zRotation);
        entity.setScale(xScale, yScale, zScale);
        textEntities.add(entity);
        return entity;
    }

    @Override
    public void setTextEntityPos(GPUTextEntity entity, float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation, float xScale, float yScale, float zScale) {
        GL33TextEntity entity1 = (GL33TextEntity)entity;
        entity1.setPosition(xPos, yPos, zPos);
        entity1.setRotation(xRotation, yRotation, zRotation);
        entity1.setScale(xScale, yScale, zScale);
    }

    @Override
    public void setTextEntityPos(GPUTextEntity entity, float xPos, float yPos, float zPos) {
        ((GL33TextEntity)entity).setPosition(xPos, yPos, zPos);
    }

    @Override
    public void setTextEntityRotation(GPUTextEntity entity, float xRotation, float yRotation, float zRotation) {
        ((GL33TextEntity)entity).setRotation(xRotation, yRotation, zRotation);
    }

    @Override
    public void setTextEntityScale(GPUTextEntity entity, float xScale, float yScale, float zScale) {
        ((GL33TextEntity)entity).setScale(xScale, yScale, zScale);
    }

    @Override
    public void setTextEntityShader(GPUTextEntity entity, GPUShader shader) {
        ((GL33TextEntity)entity).setShaderProgram((GL33Shader)shader);
    }

    @Override
    public void setTextEntityText(GPUTextEntity entity, String text, boolean centerX, boolean centerY) {
        ((GL33TextEntity)entity).setText(text, centerX, centerY);
    }

    @Override
    public Matrix4f getTextEntityTransform(GPUTextEntity entity) {
        return ((GL33TextEntity)entity).getModelViewMatrix();
    }

    @Override
    public void deleteTextEntity(GPUTextEntity entity) {
        textEntities.remove((GL33TextEntity)entity);
    }

    @Override
    public int getNumTextEntities() {
        return textEntities.size();
    }

    @Override
    public int getNumTextEntitySlots() {
        return textEntities.size();
    }

    /**
     * creates a chunk at the chunk position [x, y, z]
     *
     * @param size   how big the chunk is in each dimension
     * @param blocks a 3D array of RenderBlockModels that represent the chunk's block data.
     * @param x      the X position of the chunk
     * @param y      the Y position of the chunk
     * @param z      the Z position of the chunk
     * @return the ID of the new chunk.
     */
    @Override
    public GPUChunk spawnChunk(int size, RenderBlockModel[][][] blocks, int x, int y, int z) {
        GL33Chunk chunk = new GL33Chunk(size, blocks, x, y, z);
        chunks.add(chunk);
        chunk.build();
        chunk.sendToGPU();
        return chunk;
    }

    /**
     * sets the block data of a chunk.
     *
     * @param chunk  the chunk whose data will be set.
     * @param blocks a 3D array of RenderBlockModels that represent the chunk's block data.
     */
    @Override
    public void setChunkData(GPUChunk chunk, RenderBlockModel[][][] blocks) {
        GL33Chunk chunk1 = ((GL33Chunk)chunk);
        chunk1.setData(blocks);
        chunk1.build();
        chunk1.sendToGPU();
    }

    /**
     * sets a specific block [z, y, x] of a chunk.
     *
     * @param chunk the chunk whose block will be modified
     * @param block the RenderBlockModel of the block
     */
    @Override
    public void setChunkBlock(GPUChunk chunk, RenderBlockModel block, int x, int y, int z) {
        GL33Chunk chunk1 = ((GL33Chunk)chunk);
        chunk1.setBlock(block, x, y, z);
        chunk1.build();
        chunk1.sendToGPU();
    }

    /**
     * deletes a chunk so it is no longer rendered.
     *
     * @param chunk the ID of the chunk to remove
     */
    @Override
    public void deleteChunk(GPUChunk chunk) {
        GL33Chunk chunk1 = (GL33Chunk) chunk;
        chunks.remove(chunk1);
        chunk1.clearFromGPU();
    }

    @Override
    public int getNumChunks() {
        return chunks.size();
    }

    @Override
    public int getNumChunkSlots() {
        return chunks.size();
    }

    @Override
    public void setCameraPos(float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation) {
        cameraPosition.set(xPos, yPos, zPos);
        cameraRotation.set(xRotation, yRotation, zRotation);
        updateViewMatrix();
    }

    @Override
    public Matrix4f getCameraViewMatrix() {
        return viewMatrix;
    }

    @Override
    public Matrix4f getCameraProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Tells weather a mesh would appear on a part of the screen if it were to be rendered.
     *
     * @param mesh             the mesh to transform - will not be modified as this method creates its own copy.
     * @param meshTransform    the transform that moves the mesh in worldspace - input null to skip this transform
     * @param viewMatrix       the transform that moves the mesh around the camera - input null to skip this transform
     * @param projectionMatrix the transform that deforms the mesh to the camera projection - input null to skip this transform
     * @param x                the x position on the screen to test collision
     * @param y                the y position on the screen to test collision
     * @return weather the transformed mesh would appear on the x and y coordinates.
     */
    @Override
    public boolean meshOnScreen(CPUMesh mesh, Matrix4f meshTransform, Matrix4f viewMatrix, Matrix4f projectionMatrix, float x, float y) {
        y = -y; //The screen coordinates are mirrored for some reason

        tempMat.identity();
        if(projectionMatrix != null){
            tempMat.set(projectionMatrix);
        }
        if(viewMatrix != null){
            tempMat.mul(viewMatrix);
        }
        if(meshTransform != null){
            tempMat.mul(meshTransform);
        }

        int[] indices = mesh.indices;
        float[] positions = mesh.positions;
        // Go through each triangle
        // translate it to 2D coordinates
        //see if it collides with the position:
        //  if it does, return true
        //  if it doesn't, continue.
        //if none of the triangles collide, return false.
        for(int i=0; i<indices.length/3; i++){ //each triangle in the mesh
            //get that triangle
            tempv4f1.set(
                    positions[3*indices[3*i  ]],
                    positions[3*indices[3*i  ]+1],
                    positions[3*indices[3*i  ]+2], 1);
            tempv4f2.set(
                    positions[3*indices[3*i+1]],
                    positions[3*indices[3*i+1]+1],
                    positions[3*indices[3*i+1]+2], 1);
            tempv4f3.set(
                    positions[3*indices[3*i+2]],
                    positions[3*indices[3*i+2]+1],
                    positions[3*indices[3*i+2]+2], 1);

            //transform that triangle to the screen coordinates
            tempv4f1.mulProject(tempMat);
            tempv4f2.mulProject(tempMat); //transform the points
            tempv4f3.mulProject(tempMat);
            //if the triangle isn't behind the camera and it touches the point, return true.
            if(tempv4f1.z < 1.0f && tempv4f2.z < 1.0f && tempv4f3.z < 1.0f && isInside(tempv4f1.x, tempv4f1.y, tempv4f2.x, tempv4f2.y, tempv4f3.x, tempv4f3.y, x, y)) {
                return true;
            }
        }
        //if the point touches none of the triangles, return false.
        return false;
    }

    //thanks to https://www.tutorialspoint.com/Check-whether-a-given-point-lies-inside-a-Triangle for the following code:
    //I adapted it slightly to fit my code better, and to fix a bug related to float precision

    private static double triangleArea(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return Math.abs((p1x * (p2y - p3y) + p2x * (p3y - p1y) + p3x * (p1y - p2y)) / 2.0);
    }

    private static boolean isInside(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float x, float y) {
        double area = triangleArea (p1x, p1y, p2x, p2y, p3x, p3y) + .0000177;          ///area of triangle ABC //with a tiny bit of extra to avoid issues related to float precision errors
        double area1 = triangleArea (x, y, p2x, p2y, p3x, p3y);         ///area of PBC
        double area2 = triangleArea (p1x, p1y, x, y, p3x, p3y);         ///area of APC
        double area3 = triangleArea (p1x, p1y, p2x, p2y, x, y);        ///area of ABP

        return (area >= area1 + area2 + area3);        ///when three triangles are forming the whole triangle
        //I changed it to >= because floats cannot be trusted to hold perfectly accurate data,
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
    public int getKey(int key) {
        return window.getKey(key);
    }

    /**
     * similar to getKey, except for mouse buttons.
     *
     * @param button the button to be checked
     * @return the value of the button; see getKey for more info.
     */
    @Override
    public int getMouseButton(int button) {
        return window.getMouseButton(button);
    }

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
     * @return a timestamp, in seconds. Simply counts upwards indefinitely, not to be used to get the actual system time.
     */
    @Override
    public double getTime() {
        return System.nanoTime()/1_000_000_000.;
    }

    @Override
    public boolean shouldClose() {
        return window.windowShouldClose();
    }

    /**
     * @return true if the render method should / can be called, false otherwise.
     */
    @Override
    public boolean shouldRender() {
        return shouldRender;
    }

    /**
     * renders a frame and collects inputs
     *
     * @return the time it took to render the frame in seconds.
     */
    @Override
    public double render() {
        if(!shouldRender)return 0;
        if (window.isResized()) {
            window.setResized(false);
            glViewport(0, 0, window.getWidth(), window.getHeight());
            updateProjectionMatrix();
        }

        for(GL33Shader shader: shaders) {
            shader.bind();
            shader.setGameTime();
            shader.setProjectionMatrix(projectionMatrix);
            shader.setViewMatrix(viewMatrix);
            shader.setTextureSampler(0);
        }


        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        for(GL33Entity entity: entities){
            entity.render();
        }
        for(GL33TextEntity entity: textEntities){
            entity.render();
        }
        for(GL33Chunk chunk : chunks){
            chunk.sendToGPU();
            chunk.render();
        }
        window.update();

        return 0;
    }

    private void updateProjectionMatrix(){
        projectionMatrix.setPerspective(fov, (float)getWindowHeight()/getWindowWidth(), (float) (1./256.), 65536);
    }

    private void updateViewMatrix(){
        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate(cameraRotation.x, new Vector3f(1, 0, 0))
                .rotate(cameraRotation.y, new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
    }
}
