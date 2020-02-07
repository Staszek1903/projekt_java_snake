package client;

import common.ScoreData;
import common.Serialization;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Canvas canvas;
    @FXML
    Label label;
    @FXML
    TextField adressField;

    private Socket socket = null;
    private DatagramSocket UDPsocket = null;

    PrintWriter out = null;
    BufferedReader in = null;

    //GameLoop gameloop;
    Thread drawerThread = null;

    private int sessionID = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font theFont = Font.font( "Times New Roman", FontWeight.BOLD, 48 );
        gc.setFont( theFont );

        gc.setFill(Color.BLUE);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        //gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        //gc.fillText( "Wonsz Rzeczny", 60, 50 );
        //gc.strokeText( "Wonsz Rzeczny", 60, 50 );
        //gc.fillText( "Jest Niebezpieczny", 40, 250 );
        //gc.strokeText( "Jest Niebezpieczny", 40, 250 );

        gc.setFill( Color.RED );
    }

    public void  open_tcp() throws IOException {
        if (socket == null) {
            socket = new Socket(adressField.getText(), 5005);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }

    public void login() throws IOException {
        LoginDialog loginDialog = new LoginDialog("SNAKE LOGIN", "Login");

        Optional result = loginDialog.showAndWait();

        //wysłanie pasów
        if(result.isPresent()) {
            Pair usernamePassword = (Pair)result.get();
            out.println("login");
            out.println(usernamePassword.getKey());
            out.println(usernamePassword.getValue());
            String res = in.readLine();
            if(!res.equals("OK")){
                label.setText("LOGIN FAILED");
                throw new RuntimeException("LOGIN FAILED " + res);
            }
        }else throw new RuntimeException("Login aborted");
    }

    public void open_udp() throws SocketException {

        if(UDPsocket == null){
            UDPsocket = new DatagramSocket();

            //WYSYŁAMY DO SESJI PORT SOCKETU UDP
            //ZEBY WIEDZIALA GDZIE WYSYLAC STAN GRY
            System.out.println("wysyłam udp port " + UDPsocket.getLocalPort());
            out.println("setPortUDP");
            out.println(UDPsocket.getLocalPort());
        }
    }

    public void get_session_id() throws IOException {
        out.println("getSessionID");
        sessionID = Integer.parseInt(in.readLine());
    }

    public void create_drawer(){
        if(drawerThread == null){
            drawerThread =new GameDrawer(UDPsocket, canvas.getGraphicsContext2D(), sessionID);
            drawerThread.setDaemon(true);
            drawerThread.start();
        }
    }

    public void connect(){
        try {
            open_tcp();
            login();
            open_udp();
            get_session_id();
            create_drawer();
            label.setText("CONNECTED");

            //Alert alert = new Alert(Alert.AlertType.INFORMATION);
            //alert.setHeaderText("LOGIN SUCCESSFULL");
            //alert.showAndWait();

        }catch (Exception ex){
            ex.printStackTrace();
            disconnect();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("LOGIN FAILED");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                if(!socket.isClosed())
                    socket.close();
                socket = null;
            }

            if(drawerThread != null){
                drawerThread.interrupt();
                drawerThread = null;
            }

            if(UDPsocket != null){
                if(!UDPsocket.isClosed())
                    UDPsocket.close();
                UDPsocket = null;
            }

            label.setText("DISCONNECTED");
        }catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private KeyCode prev = null;
    public void onKeyPressed(KeyEvent event){

        String command = null;
        if(event.getCode() == KeyCode.D && prev != KeyCode.D)
            command = "turnRight";
        if(event.getCode() == KeyCode.A && prev != KeyCode.A)
            command = "turnLeft";
        if(command != null){
            System.out.println(command);
            out.println(command);
        }
        prev = event.getCode();
    }

    public void onKeyReleased(KeyEvent event){
        prev = null;
        String command = "goStraight";
        System.out.println(command);
        out.println(command);
    }

    public void register(){
        try{
            open_tcp();

            LoginDialog loginDialog = new LoginDialog("REGISTER", "Register");
            Optional<Pair<String, String>> result = loginDialog.showAndWait();
            //wysłanie pasów
            if(result.isPresent()) {
                Pair usernamePasword = (Pair) result.get();
                out.println("register");
                out.println(usernamePasword.getKey());
                out.println(usernamePasword.getValue());

                String res = in.readLine();
                if (!res.equals("OK")) {
                    label.setText("REGISTER FAILED");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("REGISTER FAILED");
                    alert.setContentText(res);
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("REGISTRATION SUCCESSFULL");
                    alert.showAndWait();
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            disconnect();
        }

    }

    public void deleteAcc(){
        if(socket != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("ACCOUNT DELETION");
            alert.setContentText("srsly bro?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() != ButtonType.OK)
                return;

            System.out.println("deleting");
            out.println("deleteAcc");
            disconnect();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("NOT LOGGEND IN");
            alert.showAndWait();
        }
    }

    public void viewStat(){

        boolean temporaryConnection = false;

        try {
            if(socket == null) {
                temporaryConnection = true;
                open_tcp();
            }

            out.println("getStat");
            ScoreData data = (ScoreData) Serialization.bytesToObj(
                    Serialization.strToBytes(in.readLine()));

            if (data == null || data.scoreData == null) return;
            ScoreDialog dialog = new ScoreDialog();
            for (Map.Entry<String, ArrayList<Pair<Integer, Timestamp>>> p : data.scoreData.entrySet()) {
                String login = p.getKey();
                ArrayList<Pair<Integer, Timestamp>> scores = p.getValue();
                for (Pair<Integer, Timestamp> pp : scores) {
                    dialog.add(login, pp.getKey(), pp.getValue());
                }
            }
            dialog.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(temporaryConnection)
                disconnect();
        }
    }

}
