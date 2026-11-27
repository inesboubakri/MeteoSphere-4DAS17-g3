package tn.esprit.ingestion.repository;

import tn.esprit.ingestion.model.MonitoredZone;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MonitoredZoneRepository extends MongoRepository<MonitoredZone, String> {
}
