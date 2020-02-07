
package server;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SymulationTask implements Callable<String> {
    private int time =0;
    private int [] zasob;

    Lock lock = new ReentrantLock();

    public SymulationTask(int time, int [] zasob)
    {
        this.time = time;
        this.zasob = zasob;
    }

    @Override
    public String call() throws Exception {

        for(int i=0; i<time; ++i)
        {
            System.out.println("thread time: "+ time + " is: " + i);
            Thread.sleep(1000);
        }

        return "SIEMA" + zasob[0];
    }
}
