package server;

import common.Location;
import common.Payment;
import common.Reservation;
import common.Treatment;
import server.network.ConcurrentServer;
import server.repository.*;
import server.service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir") + "\\src\\main\\resources\\";

        Repository<Location, Long> locationRepository = new LocationFileRepository(currentDir + "locations.txt",
                5L, true);
        Collection<Location> locations = locationRepository.getAll();
        for(Location location: locations)
            System.out.println(location);

        Repository<Treatment, Long> treatmentRepository = new TreatmentFileRepository(currentDir + "treatments.txt",
                5L, true);
        Collection<Treatment> treatments = treatmentRepository.getAll();
        for(Treatment treatment: treatments)
            System.out.println(treatment);

        Repository<Reservation, Long> reservationRepository = new ReservationFileRepository(currentDir + "reservations.txt");
        Repository<Payment, Long> paymentRepository = new PaymentFileRepository(currentDir + "payments.txt");

        LocationService locationService = new LocationService(locationRepository);
        TreatmentService treatmentService = new TreatmentService(treatmentRepository);
        PaymentService paymentService = new PaymentService(paymentRepository);
        ReservationService reservationService = new ReservationService(reservationRepository, treatmentRepository, paymentRepository);
        SupraService supraService = new SupraService(reservationService, paymentService, locationService, treatmentService);

        ConcurrentServer concurrentServer = new ConcurrentServer(55555, supraService);
        supraService.makeReservation(new Reservation(null, "Andrei", "234",
                1L, 3L, LocalDate.now(), LocalTime.now()));
        //concurrentServer.start();

    }
}
