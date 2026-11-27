package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.IngestionError;
import tn.esprit.ingestion.repository.IngestionErrorRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class IngestionErrorService extends AbstractCrudService<IngestionError> {
    private final IngestionErrorRepository repository;
    public IngestionErrorService(IngestionErrorRepository repository) { this.repository = repository; }
    @Override protected MongoRepository<IngestionError, String> repo() { return repository; }
    @Override protected String label() { return "IngestionError"; }
}
