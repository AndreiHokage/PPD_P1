package server.service;

import common.Location;
import common.Payment;
import common.Reservation;
import common.Treatment;

import java.util.Collection;

public class SupraService implements IHealthCaresServices{

    private ReservationService reservationService;
    private PaymentService paymentService;
    private LocationService locationService;
    private TreatmentService treatmentService;

    public SupraService(ReservationService reservationService,
                        PaymentService paymentService,
                        LocationService locationService,
                        TreatmentService treatmentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.locationService = locationService;
        this.treatmentService = treatmentService;
    }

    @Override
    public Boolean makeReservation(Reservation reservation) {
        return reservationService.makeReservation(reservation);
    }

    @Override
    public void makePayment(Payment payment) {
        paymentService.makePayment(payment);
    }

    @Override
    public void cancelReservation(Reservation reservation) {
        reservationService.cancelReservation(reservation);
    }

    @Override
    public Collection<Treatment> getAllTreatments() {
        return treatmentService.getAllTreatments();
    }

    @Override
    public Collection<Location> getAllLocations() {
        return locationService.getAllLocations();
    }
}
