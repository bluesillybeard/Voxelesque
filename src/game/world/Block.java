package game.world;

public interface Block {
    int SOURCE_NULL = -1;
    int SOURCE_PLAYER = 0;
    int SOURCE_EXPLOSION  = 1;
    /*
    required behavior:
    NOTE: each Block object is not a block within the world - it is a block type.
    examples: grass block, dirt block, stone block, etc.
    Thus, any instance variables in this class will apply to all blocks of its sort.
    The reason for this is to allow for data-oriented systems, as well as a unified block data system.
     */
    String getID();
    void tick(int x, int y, int z, byte[] blockData);

    void onRightClick(int source, int tool);
    void onLeftClick(int source, int tool);
    void onDestroy(int source, int tool);

}
