package tn.esprit.ingestion.service;

import tn.esprit.ingestion.model.HasId;
import tn.esprit.ingestion.exception.ResourceNotFoundException;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


public abstract class AbstractCrudService<T extends HasId> {
    protected abstract MongoRepository<T, String> repo();
    protected abstract String label();

    public List<T> findAll() { return repo().findAll(); }
    public T findById(String id) {
        return repo().findById(id).orElseThrow(() -> new ResourceNotFoundException(label() + " introuvable avec id " + id));
    }
    public T create(T entity) {
        entity.setId(null);
        return repo().save(entity);
    }
    public T update(String id, T entity) {
        if (!repo().existsById(id)) throw new ResourceNotFoundException(label() + " introuvable avec id " + id);
        entity.setId(id);
        return repo().save(entity);
    }
    public void delete(String id) {
        if (!repo().existsById(id)) throw new ResourceNotFoundException(label() + " introuvable avec id " + id);
        repo().deleteById(id);
    }
}
