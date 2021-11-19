package game.world.block;

import engine.multiplatform.gpu.GPUShader;
import engine.multiplatform.model.CPUMesh;
import game.GlobalBits;
import game.world.World;

public class VoidBlock implements Block{

    @Override
    public void destroy(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this);
    }

    @Override
    public void place(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this);
    }

    @Override
    public String getID() {
        return "voxelesque.voidBlock";
    }

    @Override
    public CPUMesh getMesh() {
        return null;
    }

    @Override
    public int getTexture() {
        return 0; //Since the model is completely empty, it makes no difference what texture is used.
    }

    @Override
    public GPUShader getShader() {
        return GlobalBits.defaultShader;
    }
}
