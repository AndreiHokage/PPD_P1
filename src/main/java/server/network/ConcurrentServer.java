package server.network;

import server.service.SupraService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConcurrentServer {

    private static final Integer NO_CHECK_SECONDS = 5;
    private static final Integer NO_END_SECONDS_SERVER = 30;
    private static final Integer NO_THREADS_CLIENTS = 11;
    private static final Integer NO_THREADS_REQUEST = 11;

    private Integer port;
    private SupraService supraService;
    private ServerSocket server = null;
    public static final ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS_CLIENTS);
    public static final ExecutorService executorRequest = Executors.newFixedThreadPool(NO_THREADS_REQUEST);
    public static Lock reentrantLock = new ReentrantLock();
    public static Condition checkSystemCondition = reentrantLock.newCondition();
    public static Boolean canCheckSystem = Boolean.TRUE;



    public ConcurrentServer(Integer port, SupraService supraService) {
        this.port = port;
        this.supraService = supraService;

        Timer timer = new Timer();
        TimeOutTask timeOutTask = new TimeOutTask(Thread.currentThread(), timer, this);
        timer.schedule(timeOutTask, NO_END_SECONDS_SERVER * 1000);
    }

    public void start(){
        try {
            long startTime = System.currentTimeMillis(); // get the current time in milliseconds

            server = new ServerSocket(port);
            executor.execute(new InspectorWorker(supraService, NO_CHECK_SECONDS));

            while(true){
                System.out.println("Waiting for clients ...");
                Socket client = server.accept();
                System.out.println("Client connected ...");
                createWorker(client, supraService);
            }
            //shutdownAndAwaitTermination(executor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createWorker(Socket client, SupraService supraService){
        Runnable worker = new Worker(client, supraService, executorRequest);
        executor.execute(worker);
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {

        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
