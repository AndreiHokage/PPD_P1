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

    private void unlockIsCancelledLock(){
        ConcurrentServer.isCancelled = true;
        ConcurrentServer.isCancelledCondition.signalAll();
        ConcurrentServer.isCancelledLock.unlock();
    }

    @Override
    public void run() {
        while(true){

            ConcurrentServer.isCancelledLock.lock();
            while(ConcurrentServer.isCancelled == true){
                try {
                    ConcurrentServer.isCancelledCondition.await();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception during the waiting in InspectorWorker => Return");
                    unlockIsCancelledLock();
                    return;
                }
            }

            try {
                supraService.checkServerStatus();
                unlockIsCancelledLock();

                TimeUnit.SECONDS.sleep(NO_WAIT_SECONDS);
            }catch (InterruptedException e){
                System.out.println("The checker thread was interrupted.");
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
