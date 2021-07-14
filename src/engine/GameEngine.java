package engine;

public class GameEngine implements Runnable {

    public static final int TARGET_UPS = 30;

    private final Window window;

    private final IGameLogic gameLogic;

    private final MouseInput mouseInput;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic) {
        window = new Window(windowTitle, width, height, vSync);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    protected void gameLoop() {
        double interval = 1f / TARGET_UPS;
        double lastUpdateTime = System.nanoTime()/1_000_000_000d;
        double lastFrameTime = System.nanoTime()/1_000_000_000d;
        boolean running = true;
        while (!window.windowShouldClose()) {

            input();

            if(System.nanoTime()/1_000_000_000d - lastUpdateTime > interval) //if enough time has passed
                update(); //run a game tick

            gameLogic.render(window);
            window.update();

            window.setTitle(1/(System.nanoTime()/1_000_000_000d - lastFrameTime) + " fps");
            lastFrameTime = System.nanoTime()/1_000_000_000d;

        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update() {
        gameLogic.update(mouseInput);
    }

}