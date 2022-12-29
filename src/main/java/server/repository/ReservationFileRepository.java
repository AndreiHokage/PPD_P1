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

    public ReservationFileRepository(String filePath){
        this.filePath = filePath;
    }

    private void writeToFile(List<Reservation> reservations){
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath, false))) {
            ObjectMapper mapper = new ObjectMapper();
            for (Reservation reservation : reservations) {
                String json = mapper.writeValueAsString(reservation);
                fileWriter.write(json + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Reservation elem) {
        Optional<Long> newAssigmentIdOptional = getAll().stream()
                .map(x -> x.getIdReservation())
                .max(Long::compare);
        Long newAssigmentId = 1L;
        if(newAssigmentIdOptional.isPresent())
            newAssigmentId = newAssigmentIdOptional.get() + 1;
        elem.setIdReservation(newAssigmentId);
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
    public void delete(Reservation elem) {
        List<Reservation> reservationsWithoutElem = getAll().stream()
                .filter(el -> !el.getIdReservation().equals(elem.getIdReservation()))
                .toList();
        writeToFile(reservationsWithoutElem);
    }

    @Override
    public void update(Reservation elem, Long id) {

    }

    @Override
    public Reservation findByID(Long id) {
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
    public Collection<Reservation> getAll() {
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
