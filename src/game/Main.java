package game;

import engine.render.LWJGLRenderer;
import engine.render.Render;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Render render = new LWJGLRenderer();
        render.init("Voxelesque 0.0.0 (alpha 0)", System.getProperty("user.dir") + "/resources/");

        GlobalBits.render = render;
        GlobalBits.playerPosition = new Vector3f();
    }
}
