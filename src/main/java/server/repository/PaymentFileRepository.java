package server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.Location;
import common.Payment;
import common.Reservation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PaymentFileRepository implements PaymentRepository{

    private String filePath;
    private Long MAX_ID = 0L;

    public PaymentFileRepository(String filePath){
        this.filePath = filePath;
        Optional<Long> newAssigmentIdOptional = getAll().stream()
                .map(x -> x.getIdPayment())
                .max(Long::compare);
        Long MAX_ID = 0L;
        if(newAssigmentIdOptional.isPresent())
            MAX_ID = newAssigmentIdOptional.get();
    }

    private synchronized void writeToFile(List<Payment> reservations){
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath, false))) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            for (Payment payment : reservations) {
                String json = mapper.writeValueAsString(payment);
                fileWriter.write(json + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void add(Payment elem) {
        MAX_ID = MAX_ID + 1;
        elem.setIdPayment(MAX_ID);
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
    public void delete(Payment elem) {

    }

    @Override
    public void update(Payment elem, Long id) {

    }

    @Override
    public synchronized Payment findByID(Long id) {
        return getAll().stream().
                filter(el -> el.getIdPayment().equals(id)).
                toList().
                get(0);
    }

    @Override
    public Iterable<Payment> findAll() {
        return null;
    }

    @Override
    public synchronized Collection<Payment> getAll() {
        Collection<Payment> payments = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            while((line = br.readLine()) != null){
                Payment payment = mapper.readValue(line, Payment.class);
                payments.add(payment);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return payments;
    }
}
