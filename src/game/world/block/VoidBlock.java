package game.world.block;

import engine.model.BlockModel;
import game.data.nbt.NBTElement;
import game.world.World;

public class VoidBlock implements Block{

    @Override
    public void onDestroy(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this);
    }

    @Override
    public String getID() {
        return "voxelesque.void";
    }

    @Override
    public BlockModel getModel() {
        return null;
    }
}
