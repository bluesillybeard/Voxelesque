package game;

import engine.multiplatform.Render;
import game.world.block.Block;
import game.world.block.VoidBlock;
import org.joml.Vector3f;

import java.util.ArrayList;

public class GlobalBits {
    public static Render render;
    public static Vector3f playerPosition;
    public static int renderDistance;
    public static ArrayList<Block> blocks;
    public static String resourcesPath;
    public static final Block voidBlock = new VoidBlock();

}
