package common;

import java.io.Serializable;
import java.util.Random;

public class Vector2f implements Serializable {
    public float x = 0, y = 0;

    public Vector2f(){
        x = 0;
        y = 0;
    }

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2f(String s){
        parse(s);
    }

    public Vector2f(float angle){ fromAngle(angle); }

    public Vector2f add(Vector2f other) {
        this.x = this.x + other.x;
        this.y = this.y + other.y;
        return this;
    }

    public Vector2f sub(Vector2f other) {
        this.x = this.x - other.x;
        this.y = this.y - other.y;
        return this;
    }

    public Vector2f mul(float s) {
        x *= s;
        y *= s;
        return this;
    }

    public Vector2f neg()
    {
        x = -x;
        y = -y;
        return this;
    }

    public float length()
    {
        return ((float)Math.sqrt(x*x + y*y));
    }

    public float distance(Vector2f other){
        Vector2f dist = cp();
        dist.sub(other);
        return dist.length();
    }

    public Vector2f normalize()
    {
        float l = this.length();
        x /= l;
        y /= l;
        return this;
    }

    public void setRand(float min, float max){
        Random r = new Random();
        this.x = min+(r.nextInt()%(max - min)) + r.nextFloat();
        this.y = min+(r.nextInt()%(max - min)) + r.nextFloat();
    }

    public Vector2f abs(){
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public Vector2f cp(){
        return new Vector2f(x,y);
    }
    public void cp(Vector2f v){
        this.x = v.x;
        this.y = v.y;
    }

    @Override
    public String toString(){
        return "{"+x+","+y+"}";
    }

    public void parse(String s) throws RuntimeException{
        int begin = s.indexOf('{');
        int middle = s.indexOf(',',begin+1);
        int end = s.indexOf('}', middle+1);

        if(begin == -1 || middle == -1 || end == -1) throw new RuntimeException(s + " is not Vector2f representation");
        String strx = s.substring(begin+1, middle);
        String stry = s.substring(middle+1, end);
        x = Float.parseFloat(strx);
        y = Float.parseFloat(stry);
    }

    public void fromAngle(float angle){
        x= (float) Math.cos(angle);
        y= (float) Math.sin(angle);
    }
}
