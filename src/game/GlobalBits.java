package game;

import engine.multiplatform.Render;
import game.world.block.Block;
import game.world.block.VoidBlock;
import org.joml.Vector3f;

import java.util.ArrayList;

public class GlobalBits {
    //technical bits
    public static float renderDistance;
    public static String resourcesPath;
    public static Render render;
    public static Vector3f tempV3f;
    public static int defaultShader;
    public static int guiShader;
    //player bits
    public static Vector3f playerPosition;
    public static Vector3f playerRotation;
    public static double sensitivity;
    //blocks
    public static ArrayList<Block> blocks;

}
