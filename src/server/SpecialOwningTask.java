package server;

import common.SpecialItem;
import common.Vector2f;

import java.util.TimerTask;

public class SpecialOwningTask extends TimerTask {
    SpecialItem specialItem;
    Vector2f snakePos;
    int sesion_id = -1;
    Vector2f f = new Vector2f();

    public SpecialOwningTask(SpecialItem specialItem, Vector2f snakePos, int sesion_id){
        this.specialItem = specialItem;
        this.snakePos = snakePos;
        this.sesion_id = sesion_id;
    }

    @Override
    public void run(){
        if(!specialItem.isOwned() && specialItem.isColliding(snakePos)){
            specialItem.setOwner(sesion_id);
        }
        if(specialItem.isOwnedBy(sesion_id)){
            if(specialItem.update(sesion_id)) {
                specialItem.setPosition(snakePos);
            }else{
                f.setRand(200,400);
                specialItem.setPosition(f);
            }
        }
    }
}
