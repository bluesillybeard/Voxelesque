package game.world.block;

import engine.model.BlockModel;
import game.world.World;

public class SimpleBlock implements Block {
    @Override
    public void onDestroy(int x, int y, int z, World world) {

    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public BlockModel getModel() {
        return null;
    }
}
