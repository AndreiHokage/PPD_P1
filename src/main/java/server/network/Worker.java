package server.network;

import common.*;
import server.service.SupraService;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;

public class Worker implements Runnable{

    private Socket client;
    private SupraService supraService;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public Worker(Socket client, SupraService supraService) {
        this.client = client;
        this.supraService = supraService;

        try {
            output=new ObjectOutputStream(client.getOutputStream());
            output.flush();
            input=new ObjectInputStream(client.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(connected){
            try{
                Request request = (Request) input.readObject();
                Response response = handleRequest(request);
                if(response != null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            client.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private Response handleRequest(Request request){
        Response response = null;
        String handlerName = "handle" + request.type();
        System.out.println("HandlerName " + handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private Response handleMAKE_RESERVATION(Request request){
        Reservation reservation = (Reservation) request.data();
        DTOReservation made = supraService.makeReservation(reservation);
        return new Response.Builder().type(ResponseType.A_MAKE_RESERVATION).data(made).build();
    }

    private Response handleMAKE_PAYMENT(Request request){
        Payment payment = (Payment) request.data();
        Payment finalPayment = supraService.makePayment(payment);
        return new Response.Builder().type(ResponseType.A_MAKE_PAYMENT).data(finalPayment).build();
    }

    private Response handleCANCEL_RESERVATION(Request request){
        Reservation reservation = (Reservation) request.data();
        supraService.cancelReservation(reservation);
        return new Response.Builder().type(ResponseType.A_CANCEL_RESERVATION).build();
    }

    private Response handleGET_ALL_TREATMENTS(Request request){
        Collection<Treatment> treatments = supraService.getAllTreatments();
        return new Response.Builder().type(ResponseType.A_GET_ALL_TREATMENTS).data(treatments).build();
    }

    private Response handleGET_ALL_LOCATIONS(Request request){
        Collection<Location> locations = supraService.getAllLocations();
        return new Response.Builder().type(ResponseType.A_GET_ALL_LOCATIONS).data(locations).build();
    }

    private Response handleSTOP(Request request){
        connected = false;
        return new Response.Builder().type(ResponseType.OK).build();
    }
}
