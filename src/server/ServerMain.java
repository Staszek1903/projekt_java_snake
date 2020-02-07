package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        Socket s = null;
        ServerSocket server = null;
        ExecutorService exec = null;
        Timer timer = null;
        System.out.println("home:" + InetAddress.getLocalHost());


        // BAZA DANYCH INIT
        String driver = "org.mariadb.jdbc.Driver";
        try {
            Class.forName(driver).newInstance();
        } catch (Exception ex) {
            System.out.println("DUPA");
            ex.printStackTrace();
        }

        String url = "jdbc:mariadb://localhost:3306/test_database";
        String user = "root";
        String password = "root";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("dupa!!!!!!");
            e.printStackTrace();
        }

        //RESOURCES
        Resources ress = new Resources(con);

        // SESJE:
        try {
            exec = Executors.newCachedThreadPool();
            System.out.println("type exit to exit");

            //TWORZENIE DEMONA OBSŁUGI SERVERA

            server = new ServerSocket(5005);
            new ServerDaemon(server,ress).start();

            //URUCHAMIANIE GŁÓWNEJ PETLI
            timer = new Timer();
            timer.scheduleAtFixedRate(new UpdateTask(ress), 500,100);

            // OCZEKIWANIE NA KLIENTÓW ORAZ TWORZENIE SESJI
            while(!server.isClosed()) {
                System.out.println("czekam na klienta");
                s = server.accept();
                System.out.println("połączono z klinetem");
                exec.submit(new Session(s, ress));
            }

        } catch (Exception exept) {
            exept.printStackTrace();

        }finally {
            exec.shutdownNow();


            if(server != null)
            {
                if(!server.isClosed())
                {
                    System.out.println("zamykam server");
                    server.close();
                }
                s = null;
            }

            if(timer != null){
                timer.cancel();
                timer.purge();
            }

            System.out.println("zakończono");
        }

 /*

        int [] zasob = new int[100];

        try{
            System.out.println(InetAddress.getLocalHost());
        }catch (Exception ex){
            ex.printStackTrace();
        }

            ExecutorService exec = Executors.newFixedThreadPool(10);
            Scanner sin = new Scanner(System.in);
            while (true) {
                System.out.print("podaj czas symulacji: ");
                int time = sin.nextInt();
                if (time == -1) break;
                exec.submit(new FutureTask<String>(new SymulationTask(time, zasob)) {
                    public void done() {
                        String ret = null;
                        try{
                            ret = (String)get();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        System.out.println("ZAKOŃCZONE: " + ret);
                    }
                });
            }

            exec.shutdown();

    }

    */


        /*Symulation s = new Symulation();
        s.setSize(10,10);
        s.addActors(10);
        s.addFood(10);

        s.printState();
*/
    }

}
