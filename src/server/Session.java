package server;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;

public class Session implements Runnable {
    private static int next_id = 0;
    private int id = 0;
    Socket socket;
    Resources resources;   // DO OTOCZENIA MUTEXEM
    SessionFuncs funcs;

    /**
     * Tworzy nowe id sesji oraz alokuje dane sesji w resourcach serwera
     * @param socket - otwarty socket do klinjenta
     * @param resources - refka na dane servera
     */
    public Session(Socket socket, Resources resources){
        this.resources = resources;
        id = next_id++;
        this.socket = socket;
        System.out.println("["+id+"] rozpoczynam sesje " + socket.getPort());
    }

    /**
     * Główna pętla sesji
     */
    @Override
    public void run() {
        try {
            // OTWIERAM STRUMIENIE SESJI
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            PrintWriter out = new PrintWriter(os, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            System.out.println("[" + id + "] pobrano strumienie");

            //TWORZE INSTANCJE WYBORU FUNKCJI
            funcs = new SessionFuncs(resources,out, in, id);

            while (true) {
                String sin = in.readLine();  //czytam linie od klienta
                if (sin == null) {           // jesli null to buffor zamkniety i zamykam sejse
                    System.out.println("[" + id + "] zamkniety buffor");
                    break;
                }

                //WYKONANIE METODY
                // sin - nazwa metody od klienta
                try{
                    Method m = SessionFuncs.class.getMethod(sin); // szukam metody i wywyoluje
                    m.invoke(funcs);
                } catch (NoSuchMethodException ex){
                    out.println("NO_SUCH_METHODE");             //odsyłam bład jesli nieznaleziono
                } catch (Exception ex){
                    ex.printStackTrace();
                    break;
                } finally {
                   // System.out.println("[" + id + "] odebrano: <" + sin + ">");
                }


            }
        }catch (Exception ex){
            System.out.println("["+id+"] exception");
            ex.printStackTrace();
        }
        finally {
            funcs.close();
            System.out.println("["+id+"] wylogowuje i zamykam");     //zamykam socket kiedy sesja sie zakonczyła
            if(!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        resources.closeSesion(id);                      // usówam dane sesji z servera
    }
}
