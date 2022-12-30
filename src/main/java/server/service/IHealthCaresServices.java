package server.service;

import common.*;

import java.util.Collection;

public interface IHealthCaresServices {
    DTOReservation makeReservation(Reservation reservation) throws Exception;

    Payment makePayment(Payment payment) throws Exception;

    void cancelReservation(Reservation reservation) throws Exception;

    Collection<Treatment> getAllTreatments() throws Exception;

    Collection<Location> getAllLocations() throws Exception;

}
