package game.world.block;

import engine.multiplatform.gpu.GPUBlock;
import engine.multiplatform.gpu.GPUShader;
import engine.multiplatform.gpu.GPUTexture;
import engine.multiplatform.model.CPUMesh;
import game.world.World;
import org.joml.Vector3i;

public interface Block extends GPUBlock {
    Block VOID_BLOCK = new VoidBlock();
    /*
    NOTE: each Block object is not a block within the world - it is a block type.
    See Stuff/World_Storage.png for a diagram.
    Thus, any instance variables in this class will apply to all blocks of its sort.
    The reason for this is to allow for data-oriented systems, so a class can be instantiated into different kinds of blocks.

     */
    void destroy(int x, int y, int z, World world);
    void place(int x, int y, int z, World world);
    void destroy(Vector3i pos, World world);
    void place(Vector3i pos, World world);
    String getID();
    CPUMesh getMesh();
    GPUTexture getTexture();
    GPUShader getShader();

}
