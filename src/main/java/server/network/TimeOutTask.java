package server.network;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

class TimeOutTask extends TimerTask {
    private Thread thread;
    private Timer timer;
    private ConcurrentServer concurrentServer;

    public TimeOutTask(Thread thread, Timer timer, ConcurrentServer concurrentServer) {
        this.thread = thread;
        this.timer = timer;
        this.concurrentServer = concurrentServer;
    }

    @Override
    public void run() {
        if (thread != null && thread.isAlive()) {
            concurrentServer.shutdownAndAwaitTermination(ConcurrentServer.executorRequest, "Requests_Pool");
            concurrentServer.shutdownAndAwaitTermination(ConcurrentServer.executor, "Clients_Pool");
            try {
                concurrentServer.closeServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            timer.cancel();
            System.out.println("FINISH SERVER");
        }
    }
}
