package server.service;

import common.Location;
import common.Treatment;
import server.repository.Repository;

import java.util.Collection;

public class TreatmentService {
    private Repository<Treatment, Long> treatmentRepository;

    public TreatmentService(Repository<Treatment, Long> treatmentRepository){
        this.treatmentRepository = treatmentRepository;
    }

    public Collection<Treatment> getAllTreatments(){
        return treatmentRepository.getAll();
    }
}
