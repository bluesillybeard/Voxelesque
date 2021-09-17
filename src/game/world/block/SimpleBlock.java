package game.world.block;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlStream;
import engine.util.StringOutputStream;
import game.GlobalBits;
import game.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlock implements Block {
    String id;
    String modID;
    int mesh;

    public SimpleBlock(String id, String modID, int mesh){
        this.id = id;
        this.modID = modID;
        this.mesh = mesh;
    }

    @Override
    public void onDestroy(int x, int y, int z, World world) {
        world.setBlock(x, y, z, GlobalBits.voidBlock);
    }

    @Override
    public String getID() {
        return modID + id;
    }

    @Override
    public int getMesh() {
        return mesh;
    }

    public static List<Block> generateBlocks(String pathToResources, String pathToBlockRegistry, String modID) {
        ArrayList<Block> blocks = new ArrayList<>();
        try {
            YamlStream stream = Yaml.createYamlInput(new File(pathToBlockRegistry)).readYamlStream();
            for (YamlNode node : stream.toList()) {
                YamlMapping blockMaps = node.asMapping();
                String id = blockMaps.string("id");
                //String name = blockMaps.string("name");
                String modelPath = blockMaps.string("model");
                int mesh = -1;
                if(!modelPath.equals("voido")) {
                    mesh = GlobalBits.render.addBlockMesh(modelPath);
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

    public static void main(String[] args) {
        //test generateBlocks
    }
}
