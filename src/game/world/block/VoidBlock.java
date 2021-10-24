package game.world.block;

import engine.multiplatform.model.CPUModel;
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
    public CPUModel getModel() {
        return null;
    }
}
