package server;

import common.Vector2f;

import java.io.Serializable;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;


class Player implements Serializable {
    private ArrayList<Vector2f> snake;
    private Vector2f dir;
    private float angle = 0;
    Direction dir_state = Direction.STRAIGHT;
    private int sesionId = 0;

    DatagramSocket datagramSocket = null;
    int clientPortUDP = -1;

    ReentrantLock lock = new ReentrantLock();

    public enum State{
        ALIVE,
        DEAD
    }

    private State state;

    private static final int SEG_LEN = 10;
    private static final float SPEED = 2.0f;

    public Player(int x, int y){
        state = State.ALIVE;
        snake = new ArrayList<Vector2f>();
        snake.add(new Vector2f(x,y));
        dir = new Vector2f(angle);
        expand();
        expand();
        expand();
        expand();
    }

    int size(){
        return snake.size();
    }

    ArrayList<Vector2f> getSegments(){
        return snake;
    }

    Vector2f getSegment(int i){
        return snake.get(i);
    }

    void expand(){
        lock.lock();
        int l = size();
        Vector2f last = snake.get(l-1);
        Vector2f next = dir.cp().neg().mul(SEG_LEN).add(last);
        snake.add(next);
        lock.unlock();
    }

    void advance(){
        lock.lock();
        int size = snake.size();
        try{
            if(snake != null && size >= 1){  //przesówam snake
                for(int i = size-1; i>0; --i){
                    snake.get(i).cp(snake.get(i-1));
                }

                Vector2f head = snake.get(0); // nowa głowa
                head.add(dir.cp().mul(SEG_LEN));

                if(head.x > 600) head.x = 0;
                if(head.y > 400) head.y = 0;
                if(head.x < 0) head.x = 600;
                if(head.y < 0) head.y = 400;
            }
        }catch (Exception ex){
            System.out.println("COŚ SIE POPSULO");
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    float getAngle(){
        return angle;
    }

    void setAngle(float angle){
        lock.lock();
        this.angle = angle;
        dir.fromAngle(angle);
        lock.unlock();
    }

    public boolean isHeadColliding(Player other){
        if(other.snake.size() == 0 || this.snake.size() == 0) return false;
        Vector2f head = snake.get(0);
        for(Vector2f point : other.snake){
            if(head.distance(point) < 10) return true;
        }

        return false;
    }



    public void setState(State st){
        lock.lock();
        state = st;
        lock.unlock();
    }

    public State getState(){
        return state;
    }

    public int getSesionId(){return sesionId;}
    public void setSesionId(int id) {
        lock.lock();
        sesionId = id;
        lock.unlock();
    }

}