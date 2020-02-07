package server;

import common.Item;
import common.SpecialItem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class Resources{
    private Map<Integer, Player> sesions = new HashMap<Integer, Player>();;
    private ArrayList <Item> items = new ArrayList<>();
    private SpecialItem special_item = new SpecialItem(50,50);
    public Connection databaseConnection;

    ReentrantLock lock = new ReentrantLock();

    public Resources(Connection con){
        databaseConnection = con;
    }

    public void registerSesion(int i){
        lock.lock();
        Player newdata = new Player(100,100);
        newdata.expand();
        sesions.put(i,newdata);
        lock.unlock();
    }

    public Player getSesion(int i) throws RuntimeException {
        if(!sesions.containsKey(i)) throw new RuntimeException("unknown sesion id");
        return sesions.get(i);
    }

    public void closeSesion(int i) throws RuntimeException{
        lock.lock();
        if(!sesions.containsKey(i)) throw new RuntimeException("unknown sesion id");
        sesions.remove(i);
        lock.unlock();
    }

    public Collection<Player> sessionIterate(){
        return sesions.values();
    }

    public int sesionsCount(){
        return sesions.size();
    }

    public int itemsCount(){return items.size(); }

    public void addItem(Item item){
        lock.lock();
        items.add(item);
        lock.unlock();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void removeItem(Item item){
        lock.lock();
        items.remove(item);
        lock.unlock();
    }

    public SpecialItem getSpecial_item() {
        return special_item;
    }
}
