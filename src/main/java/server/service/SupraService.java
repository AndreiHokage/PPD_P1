package server.service;

import common.*;

import java.time.LocalTime;
import java.util.*;

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
    public DTOReservation makeReservation(Reservation reservation) {
        return reservationService.makeReservation(reservation);
    }

    @Override
    public Payment makePayment(Payment payment) {
        paymentService.makePayment(payment);
        return payment;
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

    public void checkServerStatus() throws Exception{
        // ensure that the capacity for each treatment in each location wasn't exceeded
        for(Location location: locationService.getAllLocations()){
            for(Treatment treatment: treatmentService.getAllTreatments()){
                Integer duration = treatment.getDuration();
                Integer capacity = treatment.getMaxCapacity().get(location.getIdLocation().intValue());
                List<Reservation> reservations = reservationService.getAllReservations().stream()
                        .filter(x -> x.getIdLocation().equals(location.getIdLocation()) &&
                                     x.getIdTreatment().equals(treatment.getIdTreatment()))
                        .toList();

                List<Reservation> sortedList = reservations.stream()
                        .sorted((X, Y) -> {
                            if(X.getTimeTreatment().compareTo(Y.getTimeTreatment()) <= 0)
                                return -1;
                            return 1;
                        })
                        .toList();

                int count = 0;
                SortedSet<LocalTime> setEndTimes = new TreeSet<>();
                for(Reservation element: sortedList){
                    count = count + 1;
                    LocalTime currentTime = element.getTimeTreatment();
                    setEndTimes.add(currentTime.plusMinutes(duration));
                    SortedSet<LocalTime> tailSet = setEndTimes.headSet(currentTime);
                    for(LocalTime iop: tailSet)
                        System.out.println(currentTime + "  " + iop);
                    count = count - tailSet.size();
                    tailSet.clear();

                    if(count > capacity.intValue())
                        throw new Exception("The capacity was exceeded!");
                }
            }
        }

        List<Payment> payments = new ArrayList<>( paymentService.getALlPayments() );
        List<Payment> remainingPayments = new ArrayList<>();
        for(int i = 0; i < payments.size(); i++) {
            Boolean found = false;
            for (int j = 0; j < payments.size(); j++)
            if(i != j){
                if(payments.get(i).getIdReservation() == payments.get(j).getIdReservation()){
                    found = true;
                    break;
                }
            }

            if(!found){
                remainingPayments.add(payments.get(i));
            }
        }

    }
}
