package tn.esprit.ingestion.repository;

import tn.esprit.ingestion.model.RawObservation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RawObservationRepository extends MongoRepository<RawObservation, String> {
}
