package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.IngestionJob;
import tn.esprit.ingestion.repository.IngestionJobRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class IngestionJobService extends AbstractCrudService<IngestionJob> {
    private final IngestionJobRepository repository;
    public IngestionJobService(IngestionJobRepository repository) { this.repository = repository; }
    @Override protected MongoRepository<IngestionJob, String> repo() { return repository; }
    @Override protected String label() { return "IngestionJob"; }
}
