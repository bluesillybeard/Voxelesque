package game.world.block;

import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.RenderBlockModel;
import engine.multiplatform.render.GPUShader;
import engine.multiplatform.render.GPUTexture;
import game.world.World;

public interface Block extends RenderBlockModel {
    Block VOID_BLOCK = new VoidBlock();
    /*
    NOTE: each Block object is not a block within the world - it is a block type.
    See Stuff/World_Storage.png for a diagram.
    Thus, any instance variables in this class will apply to all blocks of its sort.
    The reason for this is to allow for data-oriented systems, so a class can be instantiated into different kinds of blocks.
    If you want to store data on a per-block basis, use NBT data.
     */
    void destroy(int x, int y, int z, World world);
    void place(int x, int y, int z, World world);
    String getID(); //eg: voxelesque:grassBlock
    CPUMesh getMesh();
    GPUTexture getTexture();
    GPUShader getShader();

}
