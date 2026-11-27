package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.HasId;
import tn.esprit.ingestion.service.AbstractCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


public abstract class AbstractCrudController<T extends HasId> {
    protected abstract AbstractCrudService<T> service();

    @GetMapping
    public List<T> getAll() { return service().findAll(); }

    @GetMapping("/{id}")
    public T getById(@PathVariable String id) { return service().findById(id); }

    @PostMapping
    public ResponseEntity<T> create(@Valid @RequestBody T entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service().create(entity));
    }

    @PutMapping("/{id}")
    public T update(@PathVariable String id, @Valid @RequestBody T entity) { return service().update(id, entity); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service().delete(id);
        return ResponseEntity.noContent().build();
    }
}
