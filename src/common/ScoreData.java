package common;

import javafx.util.Pair;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class ScoreData implements Serializable {
    //public ArrayList<Pair<String, Integer>> players = new ArrayList<>();
    //public ArrayList<Score> scores = new ArrayList<>();

    public HashMap<String, ArrayList<Pair <Integer,Timestamp>>> scoreData = new HashMap<>();//new PlayerScore();

    public void fromSQLQuery(ResultSet result) throws SQLException {
        ResultSetMetaData metadata = result.getMetaData();
        if(metadata.getColumnCount() != 3) throw new RuntimeException("Score Data query result column not 3");

        while (result.next()){
            String login = (String)result.getObject(1);
            int score = (Integer) result.getObject(2);
            Timestamp stamp = (Timestamp) result.getObject(3);

            System.out.println("ScoreData row: "+login+" score:"+ score + " stamp "+ stamp);

            if(scoreData.containsKey(login)){
                ArrayList<Pair<Integer,Timestamp>> scores = scoreData.get(login);
                scores.add(new ScoreTime(score,stamp));
            }else{
                ArrayList<Pair<Integer,Timestamp>> scores = new ArrayList<>();
                scores.add(new ScoreTime(score, stamp));
                scoreData.put(login,scores);
            }
        }

    }
}

class ScoreTime extends Pair<Integer, Timestamp>{

    public ScoreTime(Integer integer, Timestamp timestamp) {
        super(integer, timestamp);
    }

    public int getScore(){
        return getKey();
    }

    public Timestamp getTime(){
        return getValue();
    }
}

class ScoreArray extends ArrayList<ScoreTime>{

}

class PlayerScore extends HashMap<String, ScoreArray>{

}
