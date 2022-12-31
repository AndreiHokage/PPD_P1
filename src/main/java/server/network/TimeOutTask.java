package server.network;

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
            concurrentServer.shutdownAndAwaitTermination(ConcurrentServer.executorRequest);
            concurrentServer.shutdownAndAwaitTermination(ConcurrentServer.executor);
            thread.interrupt();
            timer.cancel();
        }
    }
}
