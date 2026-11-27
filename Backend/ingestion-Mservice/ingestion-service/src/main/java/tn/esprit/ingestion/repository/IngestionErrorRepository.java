package tn.esprit.ingestion.repository;

import tn.esprit.ingestion.model.IngestionError;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngestionErrorRepository extends MongoRepository<IngestionError, String> {
}
