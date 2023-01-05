package server.network;

import common.Location;
import server.service.SupraService;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
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

    private static final Integer NO_CHECK_SECONDS = 10;
    private static final Integer NO_SECONDS_END_SERVER = 1 * 60;
    private static final Integer NO_THREADS_REQUEST = 10;

    private Integer port;
    private SupraService supraService;
    private ServerSocket server = null;
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    public static final ExecutorService executorRequest = Executors.newFixedThreadPool(NO_THREADS_REQUEST);
    private Boolean running = true;

    public static Lock isCancelledLock = new ReentrantLock();
    public static Condition isCancelledCondition = isCancelledLock.newCondition();
    public static Boolean isCancelled = false;

    public ConcurrentServer(Integer port, SupraService supraService) {
        this.port = port;
        this.supraService = supraService;

        Timer timer = new Timer();
        TimeOutTask timeOutTask = new TimeOutTask(Thread.currentThread(), timer, this);
        timer.schedule(timeOutTask, NO_SECONDS_END_SERVER * 1000);
    }

    public void start(){
        try {

            server = new ServerSocket(port);
            executor.execute(new InspectorWorker(supraService, NO_CHECK_SECONDS));

            while(running){
                System.out.println("Waiting for clients ...");
                try {
                    Socket client = server.accept();
                    System.out.println("Client connected ...");
                    createWorker(client, supraService);
                } catch (SocketException e){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createWorker(Socket client, SupraService supraService){
        Runnable worker = new Worker(client, supraService, executorRequest);
        executor.execute(worker);
    }

    void shutdownAndAwaitTermination(ExecutorService pool, String namePool) {

        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool " + namePool + " did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void closeServer() throws IOException {
        server.close();
        running = false;
    }
}
