package server;

import common.Vector2f;

import java.util.ArrayList;


class Actor{
    Vector2f pos = new Vector2f(),
    dir = new Vector2f();
    float speed = 0.0f;
    int energy = 10;

    private void move()
    {
        pos = dir.mul(speed).add(pos);
    }

    private  void findDir(ArrayList<Vector2f> food){
        float min_distance = Float.MAX_VALUE;
        Vector2f min_direction = new Vector2f();

        for(Vector2f f : food){
            Vector2f dif_vect = pos.add(f.neg());
            float temp_dist = dif_vect.length();
            if(temp_dist < min_distance){
                min_distance = temp_dist;
                min_direction = dif_vect;
            }
        }

        //dir = min_direction.normalized();
    }

    public boolean act(ArrayList<Vector2f> food)
    {
        findDir(food);
        move();
        energy--;
        if(energy < 0) return false;
        return true;
    }
}

public class Symulation{

    private ArrayList<Actor> actors;
    private ArrayList<Vector2f> food;
    Vector2f board_size;


    public Symulation()
    {}

    public void setSize(int x, int y)
    {
        board_size = new Vector2f(x,y);
    }

    public void addActors(int x) throws NullPointerException
    {
        if(board_size == null) throw new NullPointerException("board_size not defined");
        actors = new ArrayList<Actor>();
        for(int i=0; i<x; ++i){
            Actor temp = new Actor();
            temp.pos.setRand(0,board_size.x);
            temp.speed = 1;
            temp.energy = 10;
            actors.add(temp);
        }
    }

    public void addFood(int x) throws NullPointerException {
        if(board_size == null) throw new NullPointerException("board_size not defined");
        food = new ArrayList<Vector2f>();
        for(int i= 0;  i<x; ++i){
            Vector2f temp = new Vector2f();
            temp.setRand(0,board_size.x);
            food.add(temp);
        }
    }

    public void iterate() throws NullPointerException
    {
        if(actors == null) throw new NullPointerException("actors not added");
        if(food == null) throw new NullPointerException("food not added");
        ArrayList<Actor> to_del = new ArrayList<Actor>();
        for(Actor a : actors) if(!a.act(food)) to_del.add(a);
        for(Actor a : to_del) actors.remove(a);
    }

    public void printState() throws NullPointerException {
        if(actors == null) throw new NullPointerException("actors not added");
        if(food == null) throw new NullPointerException("food not added");

        char [][] board = new char[(int)board_size.x][(int)board_size.y];

        for(Vector2f f: food) board[(int)f.x][(int)f.y] = '#';
        for(Actor a: actors) board[(int)a.pos.x][(int)a.pos.y] = 'A';

        for(char[] row : board) System.out.println(row);
    }

    public SimulationResult getResult()
    {
        return new SimulationResult();
    }
}
