package game;

import engine.multiplatform.Render;
import engine.multiplatform.gpu.GPUShader;
import game.misc.command.Commands;
import game.world.block.Block;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Map;

public class GlobalBits {
    //settings
    public static float renderDistance;
    public static float guiScale;

    //technical bits
    public static String resourcesPath;
    public static Commands commands;
    public static Render render;
    public static Vector3f tempV3f0;
    public static Vector3f tempV3f1;
    public static Vector3i tempV3i0;
    public static GPUShader defaultShader;
    public static GPUShader guiShader;
    //player bits
    public static Vector3f playerPosition;
    public static Vector3f playerRotation;
    public static double sensitivity;
    //blocks
    public static Map<String, Block> blocks;

}
