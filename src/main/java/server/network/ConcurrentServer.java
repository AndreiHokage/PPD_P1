package server.network;

import server.service.SupraService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConcurrentServer {

    private Integer port;
    private SupraService supraService;
    private ServerSocket server = null;

    public ConcurrentServer(Integer port, SupraService supraService) {
        this.port = port;
        this.supraService = supraService;
    }

    public void start(){
        try {
            server = new ServerSocket(port);
            while(true){
                System.out.println("Waiting for clients ...");
                Socket client = server.accept();
                System.out.println("Client connected ...");
                createWorker(client, supraService);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createWorker(Socket client, SupraService supraService){
        Runnable worker = new Worker(client, supraService);
        Thread tw = new Thread(worker);
        tw.start();
    }
}
