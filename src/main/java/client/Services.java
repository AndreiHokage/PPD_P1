package client;

import common.*;
import server.network.Request;
import server.network.RequestType;
import server.network.Response;
import server.network.ResponseType;
import server.service.IHealthCaresServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Services implements IHealthCaresServices {

    private String host;
    private int port;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public Services(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();

        initializeConnection();
    }

    @Override
    public DTOReservation makeReservation(Reservation reservation) throws Exception {
        Request request = new Request.Builder().type(RequestType.MAKE_RESERVATION).data(reservation).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() != ResponseType.A_MAKE_RESERVATION) {
            closeConnection();
            throw new Exception("Something went wrong");
        }

        DTOReservation result = (DTOReservation) response.data();
        return result;

    }

    @Override
    public Payment makePayment(Payment payment) throws Exception {
        Request request = new Request.Builder().type(RequestType.MAKE_PAYMENT).data(payment).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() != ResponseType.A_MAKE_PAYMENT) {
            closeConnection();
            throw new Exception("Something went wrong");
        }

        Payment result = (Payment) response.data();
        return result;
    }

    @Override
    public void cancelReservation(Reservation reservation) throws Exception {
        Request request = new Request.Builder().type(RequestType.CANCEL_RESERVATION).data(reservation).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() != ResponseType.A_CANCEL_RESERVATION) {
            closeConnection();
            throw new Exception("Something went wrong");
        }

        System.out.println(response.data());
    }

    @Override
    public Collection<Treatment> getAllTreatments() throws Exception {
        Request request = new Request.Builder().type(RequestType.GET_ALL_TREATMENTS).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() != ResponseType.A_GET_ALL_TREATMENTS) {
            closeConnection();
            throw new Exception("Something went wrong");
        }

        System.out.println(response.data());
        Collection<Treatment> result = (Collection<Treatment>) response.data();
        return result;
    }

    @Override
    public Collection<Location> getAllLocations() throws Exception {
        Request request = new Request.Builder().type(RequestType.GET_ALL_LOCATIONS).build();
        sendRequest(request);
        System.out.println("Send request");
        Response response = readResponse();
        System.out.println("Read response");
        if (response.type() != ResponseType.A_GET_ALL_LOCATIONS) {
            closeConnection();
            throw new Exception("Something went wrong");
        }

        System.out.println(response.data());
        Collection<Location> result = (Collection<Location>) response.data();
        return result;
    }

    public void workDone() throws Exception {
        Request request = new Request.Builder().type(RequestType.STOP).build();
        sendRequest(request);
        System.out.println("WORK DONE: Connection closed");
    }


    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object " + e);
        }
    }

    private Response readResponse() throws Exception {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    public void closeConnection() {
        try {
            workDone();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    try {
                        qresponses.put((Response) response);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    //System.out.println("Reading error " + e);
                } catch (ClassNotFoundException e) {
                    //System.out.println("Reading error " + e);
                }
            }
        }
    }
}
