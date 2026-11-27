package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.RawObservation;
import tn.esprit.ingestion.repository.RawObservationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class RawObservationService extends AbstractCrudService<RawObservation> {
    private final RawObservationRepository repository;
    public RawObservationService(RawObservationRepository repository) { this.repository = repository; }
    @Override protected MongoRepository<RawObservation, String> repo() { return repository; }
    @Override protected String label() { return "RawObservation"; }
}
