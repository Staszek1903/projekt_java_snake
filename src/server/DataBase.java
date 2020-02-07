package server;

import client.Snake;
import common.Item;
import common.ScoreData;
import javafx.util.Pair;

import java.sql.*;

public class DataBase {
    private Connection databaseConnetion;
    private Statement statement = null;

    public DataBase(Connection con) throws SQLException {
            databaseConnetion = con;
            statement = con.createStatement();

            statement.executeUpdate("use snakedb");
    }


    public void executeQuery(String query){
        try {
            ResultSet result = statement.executeQuery(query);
            ResultSetMetaData mtdt = result.getMetaData();

            int column_count = mtdt.getColumnCount();

            //for (int i = 1; i <= column_count; ++i) {
            //    System.out.print("\t" + mtdt.getColumnLabel(i) + "\t");
            //}

            while (result.next()) {
                System.out.println();
                for (int i = 1; i <= column_count; ++i) {
                    Object obj = result.getObject(i);
                    //if (obj != null) System.out.print("\t" + obj.toString() + "\t");
                    //else System.out.print("\t\t");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isIn(Object value, String column, String table){
        try {
            ResultSet result = statement.executeQuery("select " + column + " from " + table + ";");
            ResultSetMetaData mtdt = result.getMetaData();
            if(mtdt.getColumnCount() != 1) return false;



            while (result.next()){
                Object o = result.getObject(1);
                if(value.equals(o)) return true;
            }

            return false;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void createPlayersTable() throws SQLException, SnakeDatabaseException {
        if(statement.executeUpdate("create table if not exists players" +
                "(  player_id INT NOT NULL AUTO_INCREMENT,"+
                "   login VARCHAR(100) NOT NULL," +
                "   password VARCHAR(40) NOT NULL," +
                "   logged BOOLEAN NOT NULL DEFAULT FALSE," +
                "   PRIMARY KEY ( player_id ));"
                 ) == -1 ){
            throw new SnakeDatabaseException("nie można utworzyć tabeli players");
        }
    }

    public void createScoresTable() throws SQLException, SnakeDatabaseException {
        if(statement.executeUpdate("create table if not exists scores" +
                "(  score_id INT NOT NULL AUTO_INCREMENT," +
                "   player_id INT NOT NULL,"+
                "   score INT NOT NULL," +
                "   datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "   PRIMARY KEY (score_id)," +
                "   FOREIGN KEY (player_id) REFERENCES players(player_id)" +
                "   ON UPDATE CASCADE ON DELETE CASCADE);"
        ) == -1 ){
            throw new SnakeDatabaseException("nie można utworzyć tabeli scores");
        }
    }

    /*
        SW
        Moduł jest w dokumentacji do systemC XD
     */

    public void registerPlayer(String login, String password) throws SnakeDatabaseException, SQLException {
        if(isIn(login, "login", "players"))
            throw new SnakeDatabaseException("podany login już istnieje");

        if(statement.executeUpdate("insert into players " +
                "(login, password) values " +
                "(\'"+login+"\',\'"+password+"\');") == -1)
            throw new SnakeDatabaseException("nie mozna zarejstrowac");
    }

    public void checkLogin(String login, String password) throws SnakeDatabaseException, SQLException {

            ResultSet result = statement.executeQuery("select login,password,logged from players" +
                    " where login=\'"+login+"\';");
            ResultSetMetaData mtdt = result.getMetaData();
            if(mtdt.getColumnCount() != 3) throw new SnakeDatabaseException("wrong column count during login");

            if(result.first()) {
                Object pass = result.getObject(2); // get password
                Boolean logged = (Boolean)result.getObject(3);
                if (!password.equals(pass)) throw new SnakeDatabaseException("wrong password");
                if(logged) throw new SnakeDatabaseException("Already logged in");

            }else{
                throw new SnakeDatabaseException("wrong login");
            }

    }

    public void setAsLogged(String login) throws SQLException {
        statement.executeUpdate("update players set logged=TRUE where login=\'"+login+"\';");
    }

    public void setAsUnlogged(String login) throws SQLException {
        statement.executeUpdate("update players set logged=FALSE where login=\'"+login+"\';");
    }

    public void saveScores(String login, int scores) throws SQLException, SnakeDatabaseException {
        int player_id = 0;
        ResultSet result = statement.executeQuery("select player_id from players where login=\'"+login+"\';");
        if(result.first()) {
            player_id = (Integer)result.getObject(1);
            statement.executeUpdate(
                    "insert into scores (player_id,score) values " +
                            "(" + player_id + "," + scores + ");"
            );
        }else throw new SnakeDatabaseException("cannot save scores\nlogin does not exist");
    }

    public ScoreData getScores() throws SQLException {
        ScoreData data = new ScoreData();

        ResultSet result = statement.executeQuery("select login,score,datetime" +
                " from players join scores" +
                " on players.player_id = scores.player_id;");

        data.fromSQLQuery(result);

//        while (result.next()){
//            int id = (Integer)result.getObject(1);
//            String login = (String)result.getObject(2);
//            data.players.add(new Pair<>(login,id));
//        }
//
//        result = statement.executeQuery("select player_id, score, datetime from scores");
//        while(result.next()){
//            int id = (Integer)result.getObject(1);
//            int score = (Integer)result.getObject(2);
//            Timestamp o = (Timestamp) result.getObject(3);
//            data.scores.add(new ScoreData.Score(id,score,o));
//        }

        return data;
    }

    public void deleteAcc(String login) throws SQLException {
        statement.executeUpdate("delete from players where login=\'"+login+"\';");
    }

}
