package game;

import engine.multiplatform.Render;
import game.world.block.Block;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;

public class GlobalBits {
    //settings
    public static float renderDistance;
    public static float guiScale;

    //technical bits
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
    public static Map<String, Block> blocks;

}
