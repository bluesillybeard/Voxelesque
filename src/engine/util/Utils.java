package engine.util;

import engine.model.BlockMesh;
import engine.model.BlockModel;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        return(Files.readString(Path.of(fileName)));
    }




}