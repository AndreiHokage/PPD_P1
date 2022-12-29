package server.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Location;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocationFileRepository implements LocationRepository {

    private String filePath;
    private Long numberOfGeneratedLocations;
    private Boolean generate;

    public LocationFileRepository(String filePath, Long numberOfGeneratedLocations, Boolean generate){
        this.filePath = filePath;
        this.numberOfGeneratedLocations = numberOfGeneratedLocations;
        this.generate = generate;

        if(generate)
            generateLocations();
    }

    public void generateLocations(){
        ObjectMapper mapper = new ObjectMapper();
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
            for (Long i = 0L; i < numberOfGeneratedLocations; i++) {
                Location location = new Location(i, "Resort_" + i.toString());
                String json = mapper.writeValueAsString(location);
                fileWriter.write(json + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Location elem) {

    }

    @Override
    public void delete(Location elem) {

    }

    @Override
    public void update(Location elem, Long id) {

    }

    @Override
    public Location findByID(Long id) {
        return getAll().stream().
                filter(el -> el.getIdLocation().equals(id)).
                toList().
                get(0);
    }

    @Override
    public Iterable<Location> findAll() {
        return null;
    }

    @Override
    public Collection<Location> getAll() {
        Collection<Location> locations = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            ObjectMapper mapper = new ObjectMapper();
            while((line = br.readLine()) != null){
                Location location = mapper.readValue(line, Location.class);
                locations.add(location);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
