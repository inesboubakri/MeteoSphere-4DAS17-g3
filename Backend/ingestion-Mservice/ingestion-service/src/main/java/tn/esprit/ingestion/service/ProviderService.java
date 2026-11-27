package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.Provider;
import tn.esprit.ingestion.repository.ProviderRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProviderService extends AbstractCrudService<Provider> {
    private final ProviderRepository repository;
    public ProviderService(ProviderRepository repository) { this.repository = repository; }
    @Override protected MongoRepository<Provider, String> repo() { return repository; }
    @Override protected String label() { return "Provider"; }
}
