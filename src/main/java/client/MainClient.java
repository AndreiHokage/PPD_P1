package client;

public class MainClient {
    public static void main(String[] args) {
        System.out.println("Hello");

        Services services = new Services("127.0.0.1", 55555);

        Thread thr = new Thread(new ClientWorker(services));
        thr.start();
        try {
            thr.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        List<Thread> threads = new ArrayList<>(List.of(
//                new Thread(new ClientWorker(services)),
//                new Thread(new ClientWorker(services)),
//                new Thread(new ClientWorker(services)),
//                new Thread(new ClientWorker(services)),
//                new Thread(new ClientWorker(services))));
//
//        threads.forEach(Thread::start);
//
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

    }
}
