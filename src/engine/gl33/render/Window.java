package engine.gl33.render;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private String title;

    private int width, height;

    private long windowHandle;

    private boolean resized, vSync;

    private final int[] keys;
    private final int[] mouseButtons;
    private double scrolls;
    private double cursorXPos, cursorYPos;

    private static final int numKeys = 348; //there are 348 keys supported by GLFW
    private static final int numMouseButtons = 5; //there are 5 mouse buttons supported by GLFW

    public Window() {
        this.keys = new int[numKeys-1];
        this.mouseButtons = new int[numMouseButtons-1];
    }

    public void init(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //modern OpenGL
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            this.setResized(true);
        });

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS){
                keys[key] = 2;
            } else if(action == GLFW_RELEASE){
                keys[key] = 0;
            }
        });
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if(action == GLFW_PRESS){
                mouseButtons[button] = 2;
            } else if(action == GLFW_RELEASE){
                mouseButtons[button] = 0;
            }
        });
        glfwSetScrollCallback(windowHandle, (window, xOffset, yOffset) ->{
            scrolls += yOffset; // set scroll amount
        });
        glfwSetCursorPosCallback(windowHandle, (window, xPos, yPos) -> {
            cursorXPos = (xPos/this.width)*2-1; //GLFW gives pixel coordinates, but I want a nice value between -1 and 1, which maps the same way as OpenGL maps it.
            cursorYPos = (yPos/this.height)*2-1;
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        assert vidmode != null;
        glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities(); //I'm pretty sure this is a LWJGL thing, not an OpenGL thing.

        // Set the clear color

        //enable depth testing
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        //glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void close(){
        glfwTerminate();
    }
    public int getKey(int key) {
        int keyValue = keys[key];
        switch (keys[key]) {
            case 0 -> keys[key] = 1;
            case 2 -> keys[key] = 3;
        }
        return keyValue;
    }

    public int getMouseButton(int button) {
        int buttonValue = mouseButtons[button];
        if     (mouseButtons[button] == 0) mouseButtons[button] = 1;
        else if(mouseButtons[button] == 2) mouseButtons[button] = 3;
        return buttonValue;
    }

    public double getScrolls(){
        return scrolls;
    }

    public void setTitle(String title){
        glfwSetWindowTitle(windowHandle, title);
    }

    public long getWindowHandle(){
        return windowHandle;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height){
        glfwSetWindowSize(this.windowHandle, width, height);
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
        if(vSync)
            glfwSwapInterval(1);
        else
            glfwSwapInterval(0);
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public double getCursorXPos(){
        return cursorXPos;
    }

    public double getCursorYPos(){
        return cursorYPos;
    }
}
