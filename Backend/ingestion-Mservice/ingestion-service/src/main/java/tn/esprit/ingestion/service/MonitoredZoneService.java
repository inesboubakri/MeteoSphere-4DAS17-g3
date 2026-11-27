package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.MonitoredZone;
import tn.esprit.ingestion.repository.MonitoredZoneRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitoredZoneService extends AbstractCrudService<MonitoredZone> {
    private final MonitoredZoneRepository repository;
    public MonitoredZoneService(MonitoredZoneRepository repository) { this.repository = repository; }
    @Override protected MongoRepository<MonitoredZone, String> repo() { return repository; }
    @Override protected String label() { return "MonitoredZone"; }
}
