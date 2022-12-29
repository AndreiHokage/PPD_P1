package server.service;

import common.Location;
import server.repository.Repository;

import java.util.Collection;
import java.util.List;

public class LocationService {

    private Repository<Location, Long> locationRepository;

    public LocationService(Repository<Location, Long> locationRepository){
        this.locationRepository = locationRepository;
    }

    public Collection<Location> getAllLocations(){
        return locationRepository.getAll();
    }
}
