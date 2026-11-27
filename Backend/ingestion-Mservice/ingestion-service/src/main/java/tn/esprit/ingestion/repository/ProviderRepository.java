package tn.esprit.ingestion.repository;

import tn.esprit.ingestion.model.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProviderRepository extends MongoRepository<Provider, String> {
}
