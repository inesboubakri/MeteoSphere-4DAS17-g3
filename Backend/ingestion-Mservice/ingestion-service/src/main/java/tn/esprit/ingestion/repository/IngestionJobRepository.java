package tn.esprit.ingestion.repository;

import tn.esprit.ingestion.model.IngestionJob;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngestionJobRepository extends MongoRepository<IngestionJob, String> {
}
