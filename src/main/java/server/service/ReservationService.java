package server.service;

import common.Payment;
import common.Reservation;
import common.Treatment;
import server.repository.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservationService {
    private static final String SUCCEDED_APPOINTMENT = "accepted appointment";
    private static final String REJECTED_APPOINTMENT = "rejected appointment";

    private Repository<Reservation, Long> reservationRepository;
    private Repository<Treatment, Long> treatmentRepository;
    private Repository<Payment, Long> paymentRepository;

    public ReservationService(Repository<Reservation, Long> reservationRepository,
                              Repository<Treatment, Long> treatmentRepository,
                              Repository<Payment, Long> paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.treatmentRepository = treatmentRepository;
        this.paymentRepository = paymentRepository;
    }

    public Boolean makeReservation(Reservation reservation){
        Treatment treatment = treatmentRepository.findByID(reservation.getIdTreatment());
        Integer duration = treatment.getDuration();
        Integer capacity = treatment.getMaxCapacity().get(reservation.getIdLocation().intValue());
        LocalTime leftTimeReservation = reservation.getTimeTreatment();
        LocalTime rightTimeReservation = leftTimeReservation.plusMinutes(duration);

        List<Reservation> nonDisjointReservations = new ArrayList<>( reservationRepository.getAll().stream()
                .filter(el -> {
                    if(reservation.getDateTreatment().equals(el.getDateTreatment()) &&
                       reservation.getIdTreatment().equals(el.getIdTreatment()) &&
                       reservation.getIdLocation().equals(el.getIdLocation())){

                        LocalTime leftTimeElement = el.getTimeTreatment();
                        LocalTime rightTimeElement = leftTimeElement.plusMinutes(duration);

                        return (leftTimeReservation.compareTo(rightTimeElement) <= 0 &&
                           rightTimeReservation.compareTo(rightTimeElement) >=0);
                    }
                    return false;
                })
                .toList());

        nonDisjointReservations.add(reservation);
        List<Reservation> sortedList = nonDisjointReservations.stream()
                .sorted((X, Y) -> {
                    if(X.getTimeTreatment().compareTo(Y.getTimeTreatment()) <= 0)
                        return -1;
                    return 1;
                })
                .toList();
        int count = 0;
        SortedSet<LocalTime> setEndTimes = new TreeSet<>();
        for(Reservation element: sortedList){
            System.out.println("Balani " + capacity + "                      " + element);
            count = count + 1;
            LocalTime currentTime = element.getTimeTreatment();
            setEndTimes.add(currentTime.plusMinutes(duration));
            SortedSet<LocalTime> tailSet = setEndTimes.headSet(currentTime);
            for(LocalTime iop: tailSet)
                System.out.println(currentTime + "  " + iop);
            count = count - tailSet.size();
            tailSet.clear();

            if(count > capacity.intValue())
                return false;
        }

        reservationRepository.add(reservation);
        return true;
    }

    public void cancelReservation(Reservation reservation){
        Treatment treatment = treatmentRepository.findByID(reservation.getIdReservation());
        reservationRepository.delete(reservation);
        paymentRepository.add(new Payment(null, LocalDate.now(), reservation.getCnp(),
                -treatment.getPrice(), reservation.getIdReservation()));
    }
}
