package server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.Location;
import common.Reservation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ReservationFileRepository implements ReservationRepository{

    private String filePath;
    private Long MAX_ID = 0L;

    public ReservationFileRepository(String filePath){
        this.filePath = filePath;
        Optional<Long> newAssigmentIdOptional = getAll().stream()
                .map(x -> x.getIdReservation())
                .max(Long::compare);
        MAX_ID = 0L;
        if(newAssigmentIdOptional.isPresent())
            MAX_ID = newAssigmentIdOptional.get();
    }

    private synchronized void writeToFile(List<Reservation> reservations){
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath, false))) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            for (Reservation reservation : reservations) {
                String json = mapper.writeValueAsString(reservation);
                fileWriter.write(json + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void add(Reservation elem) {
        MAX_ID = MAX_ID + 1;
        elem.setIdReservation(MAX_ID);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath, true))) {
            String json = mapper.writeValueAsString(elem);
            fileWriter.write(json + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void delete(Reservation elem) {
        List<Reservation> reservationsWithoutElem = getAll().stream()
                .filter(el -> !el.getIdReservation().equals(elem.getIdReservation()))
                .toList();
        writeToFile(reservationsWithoutElem);
    }

    @Override
    public void update(Reservation elem, Long id) {

    }

    @Override
    public synchronized Reservation findByID(Long id) {
        return getAll().stream().
                filter(el -> el.getIdReservation().equals(id)).
                toList().
                get(0);
    }

    @Override
    public Iterable<Reservation> findAll() {
        return null;
    }

    @Override
    public synchronized Collection<Reservation> getAll() {
        Collection<Reservation> reservations = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            while((line = br.readLine()) != null){
                Reservation reservation = mapper.readValue(line, Reservation.class);
                reservations.add(reservation);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reservations;
    }
}
