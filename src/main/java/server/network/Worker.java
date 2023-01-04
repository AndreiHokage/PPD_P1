package server.network;

import common.*;
import server.service.SupraService;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.*;

public class Worker implements Runnable{

    private Socket client;
    private SupraService supraService;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private ExecutorService executorService;

    public Worker(Socket client, SupraService supraService, ExecutorService executorService) {
        this.client = client;
        this.supraService = supraService;
        this.executorService = executorService;

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
                Future<Response> response = handleRequest(request);
                if(response != null){
                    sendResponse(response.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " Shutdown procedure was initiated.");
                try {
                    sendResponse(handleSTOP());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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

    private class InsideWorker implements Callable{

        private Worker instance;
        private Request request;
        private Method method;

        public InsideWorker(Worker instance, Request request, Method method) {
            this.instance = instance;
            this.request = request;
            this.method = method;
        }

        @Override
        public Response call() throws Exception {
            return (Response) method.invoke(instance, request);
        }
    }

    private Future<Response> handleRequest(Request request) throws IOException {
        Future<Response> response = null;
        String handlerName = "handle" + request.type();
        System.out.println("HandlerName " + handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = executorService.submit(new InsideWorker(this, request, method));
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch(RejectedExecutionException e){ // Exception thrown by an Executor when a task cannot be accepted for execution
            System.out.println(Thread.currentThread().getName() + " Shutdown procedure was initiated.");
            sendResponse(handleSTOP());
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

    private Response handleCANCEL_RESERVATION(Request request) throws InterruptedException {
//        ConcurrentServer.reentrantLock.lock();
//        while(ConcurrentServer.canCheckSystem){
//            ConcurrentServer.checkSystemCondition.await();
//        }

        Reservation reservation = (Reservation) request.data();
        supraService.cancelReservation(reservation);

//        ConcurrentServer.canCheckSystem = Boolean.TRUE;
//        ConcurrentServer.checkSystemCondition.signalAll();
//        ConcurrentServer.reentrantLock.unlock();
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

    private Response handleSTOP(){
        connected = false;
        return new Response.Builder().type(ResponseType.A_STOP).build();
    }
}
