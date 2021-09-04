package game.world.block;

import engine.model.BlockModel;
import game.data.nbt.NBTElement;

public interface Block {
    int SOURCE_NULL = -1;
    int SOURCE_PLAYER = 0;
    int SOURCE_EXPLOSION  = 1;

    int TOOL_NULL = -1;
    int TOOL_EXPLOSION = 0;
    int TOOL_BLOCK = 1;
    int TOOL_FIST = 2;
    /*
    NOTE: each Block object is not a block within the world - it is a block type.
    See Stuff/World_Storage.png for a diagram.
    Thus, any instance variables in this class will apply to all blocks of its sort.
    The reason for this is to allow for data-oriented systems, as well as a unified block data system.
     */
    void tick(int x, int y, int z, NBTElement[] nbt /*, World world*/);

    boolean onRightClick(int x, int y, int z, NBTElement[] nbt /*, World world*/, int source, int tool);
    boolean onLeftClick(int x, int y, int z, NBTElement[] nbt /*, World world*/, int source, int tool);
    void onDestroy(int x, int y, int z, NBTElement[] nbt /*, World world*/, int source, int tool);

    String getID();
    BlockModel getModel();


}
