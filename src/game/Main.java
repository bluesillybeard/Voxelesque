package game;

import oldEngine.render.LWJGLRenderer;
import oldEngine.render.Render;
import game.world.block.Block;
import game.world.block.SimpleBlock;
import org.joml.Vector3f;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Render render = new LWJGLRenderer();
        render.init("Voxelesque 0.0.0 (alpha 0)", System.getProperty("user.dir") + "/resources/");
        GlobalBits.resourcesPath = System.getProperty("user.dir") + "/resources/";
        GlobalBits.render = render;
        GlobalBits.playerPosition = new Vector3f();
        GlobalBits.renderDistance = 500.0f;

        List<Block> blocks = SimpleBlock.generateBlocks("/home/bluesillybeard/IdeaProjects/Voxelesque/resources", "/home/bluesillybeard/IdeaProjects/Voxelesque/resources/BlockRegistry/voxelesque/blocks.yaml", "voxelesque");
        System.out.println(blocks);


        render.close();
    }
}
