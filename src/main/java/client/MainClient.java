package client;

public class MainClient {
    public static void main(String[] args) {
        Integer NO_CLIENTS = 10;

        Services[] servicesList = new Services[NO_CLIENTS];
        Thread[] threads = new Thread[NO_CLIENTS];
        for (int i = 0; i < NO_CLIENTS; i++) {
            servicesList[i] = new Services("127.0.0.1", 55555);
            threads[i] = new Thread(new ClientWorker(servicesList[i]));
            servicesList[i].setParentThread(threads[i]);
            threads[i].start();
        }

        try {
            for (int i = 0; i < NO_CLIENTS; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
