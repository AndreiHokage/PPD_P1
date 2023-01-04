package server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TransferQueue;

public class SupraService implements IHealthCaresServices{

    private class DTOInfo{
        private Location location;
        private Treatment treatment;
        private Long maximumCapacity;
        private Long actualCapacity;
        private LocalTime start;
        private LocalTime end;
        private Long reservationsId;

        public DTOInfo(Location location, Treatment treatment, Long maximumCapacity,
                       Long actualCapacity, LocalTime start, LocalTime end, Long reservationsId) {
            this.location = location;
            this.treatment = treatment;
            this.maximumCapacity = maximumCapacity;
            this.actualCapacity = actualCapacity;
            this.start = start;
            this.end = end;
            this.reservationsId = reservationsId;
        }

        @Override
        public String toString() {
            return "DTOInfo{" +
                    "location=" + location +
                    ", treatment=" + treatment +
                    ", maximumCapacity=" + maximumCapacity +
                    ", actualCapacity=" + actualCapacity +
                    ", start=" + start +
                    ", end=" + end +
                    ", reservationsId=" + reservationsId +
                    '}';
        }
    }

    private ReservationService reservationService;
    private PaymentService paymentService;
    private LocationService locationService;
    private TreatmentService treatmentService;
    private String filePathLogs;

    public SupraService(ReservationService reservationService,
                        PaymentService paymentService,
                        LocationService locationService,
                        TreatmentService treatmentService,
                        String filePathLogs) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.locationService = locationService;
        this.treatmentService = treatmentService;
        this.filePathLogs = filePathLogs;
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
//        System.out.println("(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
        // ensure that the capacity for each treatment in each location wasn't exceeded
        List<Location> locations = new ArrayList<>(locationService.getAllLocations());
        List<Treatment> treatments = new ArrayList<>(treatmentService.getAllTreatments());
        List<Payment> payments = new ArrayList<>(paymentService.getALlPayments());
        List<Reservation> reservations = new ArrayList<>(reservationService.getAllReservations());

        List<DTOInfo> dtoInfos = new ArrayList<>();
        for(Location location: locations){
            for(Treatment treatment: treatments){
                Integer duration = treatment.getDuration();
                Integer capacity = treatment.getMaxCapacity().get(location.getIdLocation().intValue());
                List<Reservation> reservationsList = reservations.stream()
                        .filter(x -> x.getIdLocation().equals(location.getIdLocation()) &&
                                     x.getIdTreatment().equals(treatment.getIdTreatment()))
                        .toList();

                List<Reservation> sortedList = reservationsList.stream()
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
                    count = count - tailSet.size();
                    tailSet.clear();

                    dtoInfos.add(new DTOInfo(location, treatment, Long.valueOf(capacity), Long.valueOf(count),
                            currentTime, currentTime.plusMinutes(duration), element.getIdReservation()));

                    if(count > capacity.intValue())
                        throw new Exception("The capacity was exceeded!");
                }
            }
        }

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

        // total amount of money for each location
        HashMap<Location, Double> sold_location = new HashMap<>();
        for(Location location: locations) sold_location.put(location, 0.0);
        for(Payment payment: remainingPayments){
            Location corresponding_location = locationService.findByID(
                    reservationService.findByID(payment.getIdReservation()).getIdLocation());
            sold_location.put(corresponding_location,
                    payment.getAmountPayed() + sold_location.get(corresponding_location));
        }

        // the list of all unpaid reservations
        List<Long> unpaidReservations = new ArrayList<>();
        for(Reservation reservation: reservations){
            Boolean found = false;
            for(Payment payment: remainingPayments){
                if(payment.getIdReservation() == reservation.getIdReservation().intValue())
                    found = true;
            }
            if(!found){
                unpaidReservations.add(reservation.getIdReservation());
            }
        }


        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePathLogs, true))) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            fileWriter.write(LocalTime.now().toString() + "\n");
            for(Map.Entry<Location, Double> keyValue: sold_location.entrySet())
                fileWriter.write(keyValue.getKey().getName() + ": " + keyValue.getValue().toString() + "\n");
            fileWriter.write("The unpaid reservation's ids:");
            for(Long elId: unpaidReservations)
                fileWriter.write(elId + " ");
            fileWriter.write("\n");
            for(DTOInfo dtoInfo: dtoInfos)
                fileWriter.write(dtoInfo + "\n");
            fileWriter.write("---------------------------------------------------------------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
    }
}
