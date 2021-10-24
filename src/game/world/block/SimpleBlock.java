package game.world.block;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlStream;
import engine.multiplatform.model.CPUModel;
import game.GlobalBits;
import game.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleBlock implements Block {
    private final String id;
    private final String modID;
    private final CPUModel model;

    public SimpleBlock(String id, String modID, CPUModel model){
        this.id = id;
        this.modID = modID;
        this.model = model;
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
    public CPUModel getModel() {
        return model;
    }

    public static ArrayList<Block> generateBlocks(String pathToResources, String pathToBlockRegistry, String modID) {
        ArrayList<Block> blocks = new ArrayList<>();
        try {
            YamlStream stream = Yaml.createYamlInput(new File(pathToResources + "/" + pathToBlockRegistry)).readYamlStream();
            for (YamlNode node : stream.toList()) {
                YamlMapping blockMaps = node.asMapping();
                String id = blockMaps.string("id");
                //String name = blockMaps.string("name");
                String modelPath = blockMaps.string("model");
                CPUModel mesh = null;
                if(!modelPath.equals("voido")) {
                    GlobalBits.render.setResourcesPath(pathToResources);
                    mesh = GlobalBits.render.loadBlockModel(modelPath);
                    GlobalBits.render.setResourcesPath(GlobalBits.resourcesPath);
                }
                blocks.add(new SimpleBlock(id, modID, mesh));
            }
        } catch(FileNotFoundException e){
            System.err.println("unable to find block registry for mod " + modID);
            return null;
        } catch(IOException e){
            System.err.println("unable to read block registry for mod " + modID);
            e.printStackTrace();
            return null;
        }
        return blocks;
    }

    public String toString(){
        return modID + ":" + id;
    }
}
