package engine.multiplatform;

import java.awt.image.BufferedImage;
import java.io.PrintStream;

public interface Render {

    /**
     * Initializes the Render and anything contained within it.
     * @param width the width of the window (800 if given an invalid width)
     * @param height the height of the window (600 if given an invalid height)
     * @param resourcesPath The path to the resources folder. This path is added at the front of any path to be loaded by the Render.
     * @param VSync Vsync
     * @param warning the warning PrintStream, Any warning will be sent through it.
     * @param error the error PrintStream. Any errors will be sent through it.
     * @param debug the debug PrintStream. Any debug messages will be sent through it.
     * @return false if something went wrong, true if all is good.
     */
    boolean init(int width, int height, String resourcesPath, boolean VSync, PrintStream warning, PrintStream error, PrintStream debug);

    BufferedImage readImage();



    double render();
}
