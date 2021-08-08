package engine.Swing;

import org.joml.Vector2f;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

public class CollisionDebugBoard extends JComponent {
    private final ArrayList<Vector2f> point1s = new ArrayList<>();
    private final ArrayList<Vector2f> point2s = new ArrayList<>();
    private final ArrayList<Vector2f> point3s = new ArrayList<>();


    public void addTriangle(float x1, float y1, float x2, float y2, float x3, float y3){
        point1s.add(new Vector2f((x1+1)*400, (-y1+1)*300));
        point2s.add(new Vector2f((x2+1)*400, (-y2+1)*300));
        point3s.add(new Vector2f((x3+1)*400, (-y3+1)*300));
    }
    public void clearTriangles(){
        point1s.clear();
        point2s.clear();
        point3s.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTriangles(g);
        //drawDonut(g);
    }

    private void drawTriangles(Graphics g){
        int[] xPoses = new int[3];
        int[] yPoses = new int[3];


        for(int i=0; i<point1s.size(); i++){
            xPoses[0] = (int)point1s.get(i).x;
            xPoses[1] = (int)point2s.get(i).x;
            xPoses[2] = (int)point3s.get(i).x;

            yPoses[0] = (int)point1s.get(i).y;
            yPoses[1] = (int)point2s.get(i).y;
            yPoses[2] = (int)point3s.get(i).y;

            g.setColor(Color.black);
            g.drawPolyline(xPoses, yPoses, 3);
            g.setColor(Color.gray);
            g.fillPolygon(xPoses, yPoses, 3);

        }
    }

    private void drawDonut(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        Dimension size = getSize();
        double w = size.getWidth();
        double h = size.getHeight();

        Ellipse2D e = new Ellipse2D.Double(0, 0, 80, 130);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.gray);

        for (double deg = 0; deg < 360; deg += 5) {
            AffineTransform at
                    = AffineTransform.getTranslateInstance(w/2, h/2);
            at.rotate(Math.toRadians(deg));
            g2d.draw(at.createTransformedShape(e));
        }
    }
}