package common;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

public class SpecialItem implements Item, Serializable {
    Vector2f pos;
    ReentrantLock lock = new ReentrantLock();
    int ownedTime = 0;
    int owner_id = -1;

    public SpecialItem(float x, float y){
        pos = new Vector2f(x,y);
    }

    @Override
    public Vector2f getPosition() {
        return pos;
    }

    public void setPosition(Vector2f pos){
        lock.lock();
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        lock.unlock();
    }

    public boolean isColliding(Vector2f other){
        lock.lock();
        boolean res = pos.distance(other) < 10;
        lock.unlock();
        return res;
    }

    public boolean isOwned(){
        lock.lock();
        boolean res = ownedTime > 0;
        lock.unlock();
        return res;
    }

    public boolean isOwnedBy(int id){
        lock.lock();
        boolean res = isOwned() && owner_id == id;
        lock.unlock();
        return res;
    }

    public boolean update(int owner_id){
        lock.lock();
        if(this.owner_id != owner_id) return false;
        if(ownedTime > 0) ownedTime--;

        System.out.println("special timer: " + ownedTime);
        lock.unlock();

        return isOwned();
    }

    public void setOwner(int owner_id){
        lock.lock();
        this.owner_id = owner_id;
        this.ownedTime = 10;
        lock.unlock();
    }
}
