package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerFuncs {
    Resources resources;
    ServerSocket serverSocket;

    public  ServerFuncs(Resources resources, ServerSocket socket){
        this.resources = resources;
        this.serverSocket = socket;
    }

    public void quit(){
        try {
            System.out.println("[Server] quiting");
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void size(){
        System.out.println("[Server] curently "+ resources.sesionsCount() + " sesions opened");
    }
}
