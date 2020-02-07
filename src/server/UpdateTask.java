package server;

import common.Food;
import common.Item;
import common.SnakesData;
import common.Vector2f;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class UpdateTask extends TimerTask {
    Resources resources;
    public UpdateTask(Resources resources){
        this.resources = resources;
    }

    @Override
    public void run() {

        Random rand = new Random();
        while (resources.itemsCount() < 3)
        {
            int x = Math.abs(rand.nextInt())%600;
            int y = Math.abs(rand.nextInt())%400;
            resources.addItem(new Food(new Vector2f(x,y)));
        }

        SnakesData data = new SnakesData();
        data.items = resources.getItems();
        data.special_item = resources.getSpecial_item();

        for (Player player : resources.sessionIterate()) {
            if(player.getState() == Player.State.DEAD)
                continue;

            player.advance();

            Vector2f head = player.getSegments().get(0);

            ArrayList<Item> to_erase = new ArrayList<>();
            for(Item i : resources.getItems()){
                if(i.getPosition().distance(head) < 20){
                    player.expand();
                    to_erase.add(i);
                }
            }
            for(Item i : to_erase){
                resources.removeItem(i);
            }

            if (player.dir_state == Direction.RIGHT) {
                float a = player.getAngle();
                player.setAngle(a + 0.25f);
            }

            if (player.dir_state == Direction.LEFT) {
                float a = player.getAngle();
                player.setAngle(a - 0.25f);
            }

            for (Player other_player : resources.sessionIterate()) {
                if(player == other_player) continue;
                if(player.isHeadColliding(other_player) && other_player.getState() != Player.State.DEAD){
                    player.setState(Player.State.DEAD);
                }
            }

            data.addSnake(player.getSegments(), player.getSesionId());
        }



        if (data.snakesData != null) {
            byte[] datagramData = data.toBytes();

            for (Player p : resources.sessionIterate()) {
                if (p.datagramSocket != null) {
                    try {
                        DatagramPacket packet = new DatagramPacket(datagramData, datagramData.length,
                                InetAddress.getLocalHost(), p.clientPortUDP);
                        p.datagramSocket.send(packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

}

