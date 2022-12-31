package server.network;

import server.service.SupraService;

import java.util.concurrent.TimeUnit;

public class InspectorWorker implements Runnable{

    private SupraService supraService;
    private Integer NO_WAIT_SECONDS;

    public InspectorWorker(SupraService supraService, Integer NO_WAIT_SECONDS) {
        this.supraService = supraService;
        this.NO_WAIT_SECONDS = NO_WAIT_SECONDS;
    }

    @Override
    public void run() {
        while(true){
            try {
//                ConcurrentServer.reentrantLock.lock();
//                while(!ConcurrentServer.canCheckSystem){
//                    ConcurrentServer.checkSystemCondition.await();
//                }

                supraService.checkServerStatus();

//                ConcurrentServer.canCheckSystem = Boolean.FALSE;
//                ConcurrentServer.checkSystemCondition.signalAll();
//                ConcurrentServer.reentrantLock.unlock();
                TimeUnit.SECONDS.sleep(NO_WAIT_SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
