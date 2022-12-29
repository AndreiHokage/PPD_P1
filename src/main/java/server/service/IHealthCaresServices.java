package server.service;

import common.Location;
import common.Payment;
import common.Reservation;
import common.Treatment;

import java.util.Collection;

public interface IHealthCaresServices {
    Boolean makeReservation(Reservation reservation);

    void makePayment(Payment payment);

    void cancelReservation(Reservation reservation);

    Collection<Treatment> getAllTreatments();

    Collection<Location> getAllLocations();

}
