package game.world.block;

import game.world.World;

public interface Block {
    Block VOID_BLOCK = new VoidBlock();
    /*
    NOTE: each Block object is not a block within the world - it is a block type.
    See Stuff/World_Storage.png for a diagram.
    Thus, any instance variables in this class will apply to all blocks of its sort.
    The reason for this is to allow for data-oriented systems, so a class can be instantiated into different kinds of blocks.

     */
    void onDestroy(int x, int y, int z, World world);

    String getID();
    int getMesh();
}
