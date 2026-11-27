package tn.esprit.ingestion.service;


import tn.esprit.ingestion.model.Provider;
import tn.esprit.ingestion.repository.ProviderRepository;
import tn.esprit.ingestion.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {
    @Mock ProviderRepository repository;
    @InjectMocks ProviderService service;

    @Test void findAllReturnsProviders() {
        Provider p = new Provider(); p.setNom("TestProvider");
        when(repository.findAll()).thenReturn(List.of(p));
        assertEquals(1, service.findAll().size());
    }
    @Test void findByIdReturnsProvider() {
        Provider p = new Provider(); p.setId("p1");
        when(repository.findById("p1")).thenReturn(Optional.of(p));
        assertEquals("p1", service.findById("p1").getId());
    }
    @Test void findByIdThrowsWhenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById("missing"));
    }
    @Test void createClearsIdBeforeSave() {
        Provider p = new Provider(); p.setId("old");
        when(repository.save(any(Provider.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Provider created = service.create(p);
        assertNull(created.getId());
        verify(repository).save(p);
    }
    @Test void updateSetsIdAndSavesWhenExists() {
        Provider p = new Provider();
        when(repository.existsById("p1")).thenReturn(true);
        when(repository.save(any(Provider.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertEquals("p1", service.update("p1", p).getId());
    }
    @Test void deleteCallsRepositoryWhenExists() {
        when(repository.existsById("p1")).thenReturn(true);
        service.delete("p1");
        verify(repository).deleteById("p1");
    }
}
