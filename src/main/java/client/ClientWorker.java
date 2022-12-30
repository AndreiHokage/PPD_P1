package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.Location;
import common.Payment;
import common.Reservation;
import common.Treatment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ClientWorker extends Thread {

    private Services services;
    private Random rand = new Random();
    private long paymentIdNumber = 0;
    private long reservationIdNumber = 0;

    public ClientWorker(Services services) {
        this.services = services;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("\nNEW RESERVATION\n");

                Reservation reservation = createRandomReservation();
                Boolean response = services.makeReservation(reservation);
                if (response) {
                    System.out.println("Successful reservation");

                    int cancelReservation = Math.abs(rand.nextInt()) % 2;

                    Payment payment = createPayment(reservation);

                    System.out.println("Payment " + payment + " initialized");
                    services.makePayment(payment);
                    System.out.println("Payment confirmed");

                    if (cancelReservation == 1) {
                        services.cancelReservation(reservation);
                        System.out.println("Reservation canceled");
                    } else System.out.println("Reservation ok");
                } else
                    System.out.println("Unsuccessful reservation");

                TimeUnit.SECONDS.sleep(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        services.closeConnection();
    }

    private Payment createPayment(Reservation reservation) throws Exception {
        try {
            double cost = 0;
            Treatment t = services.getAllTreatments()
                    .stream()
                    .filter(treatment -> treatment.getIdTreatment().equals(reservation.getIdTreatment()))
                    .findFirst()
                    .orElse(null);
            if (t != null)
                cost = t.getPrice();

            paymentIdNumber++;
            return new Payment(
                    paymentIdNumber,
                    LocalDate.now(),
                    reservation.getCnp(),
                    cost,
                    reservation.getIdReservation());
        } catch (Exception e) {
            throw e;
        }
    }

    private Reservation createRandomReservation() throws Exception {
        try {
            List<Location> locations = (List<Location>) services.getAllLocations();
            Location location = locations.get(Math.abs(rand.nextInt()) % locations.size());

            List<Treatment> treatments = (List<Treatment>) services.getAllTreatments();
            Treatment treatment = treatments.get(Math.abs(rand.nextInt()) % treatments.size());

            reservationIdNumber++;
            return new Reservation(reservationIdNumber,
                    currentThread().getName(),
                    String.valueOf(currentThread().getId()),
                    location.getIdLocation(),
                    treatment.getIdTreatment(),
                    LocalDate.now(),
                    LocalTime.now());
        } catch (Exception e) {
            throw e;
        }
    }
}
