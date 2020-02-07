package client;

import common.Item;
import common.SnakesData;
import common.Vector2f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class GameDrawer extends Thread {

    SnakesData data = new SnakesData();
    Snake snake = new Snake();

    DatagramSocket socket = null;
    byte [] packetdata = new byte[65536];
    DatagramPacket packet = new DatagramPacket(packetdata, packetdata.length);
    GraphicsContext gc = null;

    Vector2f screenSize;
    private int sessionID = 0;

    public GameDrawer (DatagramSocket socket, GraphicsContext gc, int sessionID){
        this.socket = socket;
        this.gc = gc;
        this.sessionID = sessionID;
        screenSize = new Vector2f((float)gc.getCanvas().getWidth(),
                (float)gc.getCanvas().getHeight());
    }

    @Override
    public void run() {
        while (true){
            if(Thread.currentThread().isInterrupted()){
                socket.close();
                System.out.println("Draw thread interrupted");
                break;
            }



            if(socket != null) {
                try {
                    socket.receive(packet);
                    data.formBytes(packetdata);

                    if(data.sesion_index == null ||
                            data.snakesData == null ||
                            data.items == null )
                        continue;

                    //System.out.println(data.snakesData.size());
                    //System.out.println(data.sesion_index);
                    //System.out.println(sessionID);

                    int player_index = data.sesion_index.get(sessionID);

                    gc.setFill(Color.BLUE);
                    gc.fillRect(0,0,screenSize.x,screenSize.y);

                    for(int i =0 ; i<data.snakesData.size(); ++i){
                        ArrayList<Vector2f> sn  =  data.snakesData.get(i);
                        snake.segs = sn;

                        snake.c_circles = (i == player_index)? Color.BLUE : Color.YELLOW;
                        snake.draw(gc);
                    }

                    if(data.items != null){
                        for(Item i : data.items){
                            Vector2f pos = i.getPosition();

                            gc.setFill(Color.RED);
                            gc.fillOval(pos.x - 10, pos.y-10,20,20);
                        }
                    }

                    if(data.special_item != null){
                        Vector2f pos = data.special_item.getPosition();
                        gc.setFill(Color.BLACK);
                        gc.fillOval(pos.x - 10, pos.y-10,20,20);
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
