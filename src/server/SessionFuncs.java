package server;

import common.ScoreData;
import common.Serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.sql.SQLException;
import java.util.Timer;

public class SessionFuncs {
    Resources resources;
    Player player = null;
    PrintWriter out;
    BufferedReader in;
    int sesion_id =0;
    DataBase database;
    String currenLogin = "";
    Timer timer = null;


    public SessionFuncs(Resources resources, PrintWriter out, BufferedReader in, int id){
        this.resources = resources;
        this.out = out;
        this.in = in;
        sesion_id = id;
        try {
            database = new DataBase(resources.databaseConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        logout();
        saveScores();
    }

    public void size(){
        Player p = resources.getSesion(sesion_id);
        out.println(p.size());
    }

    public void seg(){
        Player player = resources.getSesion(sesion_id);
        for(int i = 0; i< player.size(); ++i){
            out.println(player.getSegment(i));
        }
    }

    public void getSessionID(){ out.println(sesion_id);}

    public void sessionCount(){
        out.println(resources.sesionsCount());
    }

    public void turnRight(){
        Player player = resources.getSesion(sesion_id);
        player.dir_state = Direction.RIGHT;
        System.out.println("[" + sesion_id + "] right");
    }

    public void goStraight(){
        Player player = resources.getSesion(sesion_id);
        player.dir_state = Direction.STRAIGHT;
        System.out.println("[" + sesion_id + "] straight");
    }

    public void turnLeft(){
        Player player = resources.getSesion(sesion_id);
        player.dir_state = Direction.STRAIGHT.LEFT;
        System.out.println("[" + sesion_id + "] left");
    }

    public void setPortUDP(){
        try {
            String s_port = in.readLine();
            System.out.println("[" + sesion_id + "] odebrano port udp " + s_port);
            player.datagramSocket = new DatagramSocket();
            player.clientPortUDP = Integer.parseInt(s_port);
            System.out.println("[" + sesion_id + "] utworzono socket UDP " + player.datagramSocket.getLocalPort());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void login(){
        try {
            String login = in.readLine();
            String pasw = in.readLine();
            System.out.println("[" + sesion_id + "] login: " +login + " haslo: " + pasw);

            database.checkLogin(login,pasw);
            resources.registerSesion(sesion_id);
            player = resources.getSesion(sesion_id);
            player.setSesionId(sesion_id);

            database.setAsLogged(login);
            out.println("OK");
            currenLogin = login;

            timer = new Timer();
            timer.scheduleAtFixedRate(new SpecialOwningTask(resources.getSpecial_item(),player.getSegment(0), sesion_id), 500,100);

        }catch (Exception ex){
            ex.printStackTrace();
            out.println("LOGIN ERROR "+ ex.getMessage());
        }
    }

    public void logout(){
        try {
            if(database.isIn(currenLogin,"login","players")){
                database.setAsUnlogged(currenLogin);
            }

            timer.cancel();
            timer.purge();

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void register(){
        try{
            String login = in.readLine();
            String pasw = in.readLine();

            database.createPlayersTable();
            database.registerPlayer(login,pasw);
            out.println("OK");
            System.out.println("[" + sesion_id + "] registered: " +login + " haslo: " + pasw);

        }catch (Exception ex){
            ex.printStackTrace();
            out.println("NOT REGISTERED " + ex.getMessage());
            System.out.println("[" + sesion_id + "] failed to register");
        }
    }

    public void saveScores(){
        try {
            database.createScoresTable();
            int score = player.getSegments().size();
            database.saveScores(currenLogin, score);
            System.out.println("[" + sesion_id + "] saving scores");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SnakeDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void deleteAcc(){
        System.out.println("[" + sesion_id + "] deleting account");
        try {
            database.deleteAcc(currenLogin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getStat(){
        //out.println("to implement");
        System.out.println("[" + sesion_id + "] transmitting stats");

        try {
            ScoreData data = database.getScores();
            String str = Serialization.bytesToStr(Serialization.objToBytes((Serializable) data));
            System.out.println(str);
            out.println(str);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
}
