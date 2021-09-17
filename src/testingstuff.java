import com.amihaiemil.eoyaml.*;
import engine.util.StringOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class testingstuff {
    public static void main(String[] args) throws IOException {
        YamlStream stream = Yaml.createYamlInput(new File("/home/bluesillybeard/IdeaProjects/Voxelesque/resources/BlockRegistry/voxelesque/blocks.yaml")).readYamlStream();
        for(YamlNode node: stream.toList()){
            YamlMapping blockMaps = node.asMapping();
            System.out.println("the ID is" + blockMaps.string("id"));
            System.out.println("the name is" + blockMaps.string("name"));
            System.out.println("the model is" + blockMaps.string("model"));
        }
    }
}
