package server;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Scanner;

public class ServerDaemon extends Thread{
    private ServerSocket serverSocket;
    private Resources resources;


    public ServerDaemon(ServerSocket serv_soc, Resources resources){
        this.serverSocket = serv_soc;
        this.resources = resources;
        setDaemon(true);
    }

    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        ServerFuncs funcs = new ServerFuncs(resources, serverSocket);

        while(true){
            String nl = sc.nextLine();

            try {
                Method m = ServerFuncs.class.getMethod(nl);
                m.invoke(funcs);
            }catch (NoSuchMethodException ex){
                System.out.println("[Server] nieznane polecenie <"+nl+">");
            }catch (Exception ex){
                ex.printStackTrace();
            }


        }
    }
}
