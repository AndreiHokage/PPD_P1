package server.service;

import common.Location;
import common.Payment;
import common.Reservation;
import common.Treatment;

import java.util.Collection;

public interface IHealthCaresServices {
    Boolean makeReservation(Reservation reservation) throws Exception;

    void makePayment(Payment payment) throws Exception;

    void cancelReservation(Reservation reservation) throws Exception;

    Collection<Treatment> getAllTreatments() throws Exception;

    Collection<Location> getAllLocations() throws Exception;

}
