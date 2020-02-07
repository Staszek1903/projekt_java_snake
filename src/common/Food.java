package common;

import java.io.Serializable;

public class Food implements Item, Serializable {
    Vector2f pos = null;

    public Food(Vector2f pos){
        this.pos = pos;
    }

    @Override
    public Vector2f getPosition() {
        return pos;
    }
}
