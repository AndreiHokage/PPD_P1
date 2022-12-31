package server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.Location;
import common.Treatment;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TreatmentFileRepository implements TreatmentRepository{

    private String pathFile;
    private Long numberOfGeneratedTreatment;
    private Boolean generate;

    public TreatmentFileRepository(String pathFile, Long numberOfGeneratedTreatment, Boolean generate){
        this.pathFile = pathFile;
        this.numberOfGeneratedTreatment = numberOfGeneratedTreatment;
        this.generate = generate;

        if(this.generate)
            generateTreatments();
    }

    private void generateTreatments(){
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Double> prices = new ArrayList<>(Arrays.asList(50.0, 20.0, 40.0, 100.0, 30.0));
        ArrayList<Integer> duration = new ArrayList<>(Arrays.asList(120, 20, 30, 60, 30));
        ArrayList<Integer> maxInitialCapacity = new ArrayList<>(Arrays.asList(3, 1, 1, 2, 1));
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(pathFile))) {
            for (Long i = 0L; i < numberOfGeneratedTreatment; i++) {
                List<Integer> capacity = new ArrayList<>(Arrays.asList(maxInitialCapacity.get(i.intValue())));
                Integer prev = maxInitialCapacity.get(i.intValue());
                for (int j = 1; j < 5; j++) {
                    Integer newCapacity = prev * j;
                    capacity.add(newCapacity);
                }

                Treatment treatment = new Treatment(i, prices.get(i.intValue()),
                        duration.get(i.intValue()), capacity);
                String json = mapper.writeValueAsString(treatment);
                fileWriter.write(json + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Treatment elem) {

    }

    @Override
    public void delete(Treatment elem) {

    }

    @Override
    public void update(Treatment elem, Long id) {

    }

    @Override
    public synchronized Treatment findByID(Long id) {
        return getAll().stream().
                filter(el -> el.getIdTreatment().equals(id)).
                toList().
                get(0);
    }

    @Override
    public Iterable<Treatment> findAll() {
        return null;
    }

    @Override
    public synchronized Collection<Treatment> getAll() {
        Collection<Treatment> treatments = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(pathFile))){
            String line;
            ObjectMapper mapper = new ObjectMapper();
            while((line = br.readLine()) != null){
                Treatment treatment = mapper.readValue(line, Treatment.class);
                treatments.add(treatment);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return treatments;
    }
}
