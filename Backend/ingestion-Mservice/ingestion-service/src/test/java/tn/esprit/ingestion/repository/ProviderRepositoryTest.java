package tn.esprit.ingestion.repository;


import tn.esprit.ingestion.model.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import static org.junit.jupiter.api.Assertions.*;


@DataMongoTest
class ProviderRepositoryTest {
    @Autowired ProviderRepository repository;

    @Test void saveAndFindProvider() {
        Provider provider = new Provider();
        provider.setNom("TestMongoProvider");
        provider.setUrlBase("https://example.test");
        Provider saved = repository.save(provider);
        assertNotNull(saved.getId());
        assertTrue(repository.findById(saved.getId()).isPresent());
        repository.deleteById(saved.getId());
    }
}
