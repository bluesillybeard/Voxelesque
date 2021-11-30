package game.world.block;

import engine.multiplatform.gpu.GPUShader;
import engine.multiplatform.gpu.GPUTexture;
import engine.multiplatform.model.CPUMesh;
import game.GlobalBits;
import game.world.World;
import org.joml.Vector3i;

public class VoidBlock implements Block{

    @Override
    public void destroy(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this, true);
    }

    @Override
    public void place(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this, true);
    }

    @Override
    public void destroy(Vector3i pos, World world) {
        world.setBlock(pos, this, true);
    }

    @Override
    public void place(Vector3i pos, World world) {
        world.setBlock(pos, this, true);
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
    public GPUTexture getTexture() {
        return null;
    }

    @Override
    public GPUShader getShader() {
        return null;
    }
}
