package game;

import engine.gl33.GL33Render;
import engine.multiplatform.Render;
import game.world.block.Block;
import game.world.block.SimpleBlock;
import org.joml.Vector3f;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Render render = new GL33Render();
        if (!render.init("Voxelesque Engine Test Game", 800, 600, args[0], true, System.err, System.err, System.out, (float) Math.toRadians(80))) {
            GlobalBits.resourcesPath = System.getProperty("user.dir") + "/resources";
            render.setResourcesPath(GlobalBits.resourcesPath);
            GlobalBits.render = render;
            GlobalBits.playerPosition = new Vector3f();
            GlobalBits.renderDistance = 6f;

            List<Block> blocks = SimpleBlock.generateBlocks("/home/bluesillybeard/IdeaProjects/Voxelesque/resources", "/home/bluesillybeard/IdeaProjects/Voxelesque/resources/BlockRegistry/voxelesque/blocks.yaml", "voxelesque");
            System.out.println(blocks);
        }
    }
}
