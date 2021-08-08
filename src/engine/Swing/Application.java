package engine.Swing;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Application /*extends JFrame*/ {
    CollisionDebugBoard board;
    JFrame frame;
    public Application() {

        initUI();
    }

    private void initUI() {
        frame = new JFrame();
        board = new CollisionDebugBoard();
        board.addTriangle(0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f);
        frame.add(board);

        frame.setSize(800, 600);

        frame.setTitle("Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Application ex = new Application();
        });
    }
}