package common;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SnakesData implements Serializable{

    public ArrayList<ArrayList<Vector2f>> snakesData = null;
    public ArrayList<Item> items = null;
    public HashMap<Integer,Integer> sesion_index = null;
    public Item special_item = null;

    public void addSnake(ArrayList<Vector2f> s, int sessionID){
        if(snakesData == null)
            snakesData = new ArrayList<>();
        if(sesion_index == null)
            sesion_index = new HashMap<>();

        snakesData.add(s);
        sesion_index.put(sessionID, snakesData.size()-1);
    }

    public byte [] toBytes(){
        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(65526);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void formBytes(byte [] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            SnakesData temp = (SnakesData) is.readObject();
            snakesData = temp.snakesData;
            items = temp.items;
            sesion_index = temp.sesion_index;
            special_item = temp.special_item;
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}

/*KODY ATOMOWE
207534, 43198,  30566,  171576, 271048, 275085, 172898,
169360, 268281, 128980, 258133, 203729, 201937, 169754,
191390, 150416, 250981, 267604, 274555, 137879, 277889,
140416, 238739, 228865, 285275, 284372, 228626, 166671,
64990,  284338, 81974,  241142, 100938, 206964, 267270,
244885, 237600, 94592,  209604, 101359, 118786, 210391,
287190, 287158, 287535, 271409, 264855, 247426, 112011,
267035, 241228, 287703, 257095, 171875, 284286

 */