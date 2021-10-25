package game.world.block;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlStream;
import engine.multiplatform.model.CPUMesh;
import engine.multiplatform.model.CPUModel;
import game.GlobalBits;
import game.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlock implements Block {
    private final String id;
    private final String modID;
    private final CPUMesh mesh;
    private final int texture;
    private final int shader;

    public SimpleBlock(String id, String modID, CPUMesh mesh, int texture, int shader){
        this.id = id;
        this.modID = modID;
        this.mesh = mesh;
        this.texture = texture;
        this.shader = shader;
    }

    @Override
    public void destroy(int x, int y, int z, World world) {
        world.setBlock(x, y, z, Block.VOID_BLOCK);
    }

    @Override
    public void place(int x, int y, int z, World world) {
        world.setBlock(x, y, z, this);
    }

    @Override
    public String getID() {
        return modID + id;
    }

    @Override
    public CPUMesh getMesh() {
        return mesh;
    }

    @Override
    public int getTexture() {
        return texture;
    }

    @Override
    public int getShader() {
        return shader;
    }

    public static ArrayList<Block> generateBlocks(String pathToResources, String pathToBlockRegistry, String modID) {
        GlobalBits.render.setResourcesPath(pathToResources); //set render resource path the mod resource path
        ArrayList<Block> blocks = new ArrayList<>();
        try {
            YamlStream stream = Yaml.createYamlInput(new File(pathToResources + "/" + pathToBlockRegistry)).readYamlStream();
            List<CPUModel> blockModels = new ArrayList<>();
            ArrayList<String> blockIDs = new ArrayList<>();

            for (YamlNode node : stream.toList()) {
                YamlMapping blockMaps = node.asMapping();
                String id = blockMaps.string("id");
                //String name = blockMaps.string("name");
                String modelPath = blockMaps.string("model");

                blockModels.add(GlobalBits.render.loadBlockModel(modelPath));
                blockIDs.add(id);
            }
            blockModels = GlobalBits.render.generateImageAtlas(blockModels);
            int texture = GlobalBits.render.readTexture(blockModels.get(0).texture);
            for(int i=0; i<blockModels.size(); i++){
                blocks.add(new SimpleBlock(blockIDs.get(i), modID, blockModels.get(i).mesh, texture, GlobalBits.defaultShader));
            }
        } catch(FileNotFoundException e){
            System.err.println("unable to find simpleBlock registry for mod " + modID);
            return null;
        } catch(IOException e){
            System.err.println("unable to read simpleBlock registry for mod " + modID);
            return null;
        }
        GlobalBits.render.setResourcesPath(GlobalBits.resourcesPath); //set render resource path back to vanilla path
        return blocks;
    }

    public String toString(){
        return modID + ":" + id;
    }
}
