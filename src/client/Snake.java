package client;

import common.Vector2f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Snake {
    public ArrayList<Vector2f> segs = new ArrayList<Vector2f>();
    public Color c_body = Color.GREEN;
    public Color c_circles = Color.YELLOW;

    void draw(GraphicsContext gc){
        Vector2f dirfront = segs.get(0).cp().add(segs.get(1).cp().neg());
        Vector2f dirback = segs.get(segs.size()-1).cp().add(segs.get(segs.size()-2).cp().neg());

        drawTriangle(segs.get(0), dirfront,gc);
        drawTriangle(segs.get(segs.size()-1), dirback,gc);



        for(int i=0; i<segs.size()-1; ++i)
        {
            Vector2f pos1 = segs.get(i);
            Vector2f pos2 = segs.get(i+1);
            drawBodySeg(pos1,pos2, gc);
        }

        for(Vector2f a : segs){
            gc.setFill(c_circles);
            gc.fillOval(a.x-4, a.y-4, 8, 8);
        }
    }

    void drawTriangle(Vector2f pos, Vector2f dir, GraphicsContext gc){
        double [] TriangleX = new double[3];
        double [] TriangleY = new double[3];

        TriangleX[0] = -dir.y + pos.x;
        TriangleY[0] = dir.x  + pos.y;
        TriangleX[1] = dir.y + pos.x;
        TriangleY[1] = -dir.x + pos.y;
        TriangleX[2] = dir.x + pos.x;
        TriangleY[2] = dir.y + pos.y;

        gc.setFill(c_body);
        gc.fillPolygon(TriangleX,TriangleY,3);
    }

    void drawBodySeg(Vector2f pos1, Vector2f pos2, GraphicsContext gc){
        double [] segX = new double[4];
        double [] segY = new double[4];

        Vector2f dir = pos1.cp().neg().add(pos2);

        if(dir.length() > 20) return;

        segX[0] = -dir.y + pos1.x;
        segY[0] = dir.x + pos1.y;
        segX[1] = dir.y + pos1.x;
        segY[1] = -dir.x + pos1.y;

        dir.neg();

        segX[2] = -dir.y + pos2.x;
        segY[2] = dir.x + pos2.y;
        segX[3] = dir.y + pos2.x;
        segY[3] = -dir.x + pos2.y;

        gc.setFill(c_body);
        gc.fillPolygon(segX,segY,4);
    }

    public void fromBytes(byte [] data){
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            segs = (ArrayList<Vector2f>) is.readObject();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
