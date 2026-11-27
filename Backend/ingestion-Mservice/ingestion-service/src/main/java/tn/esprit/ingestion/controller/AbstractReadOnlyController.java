package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.HasId;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Contrôleur en lecture seule pour les ressources produites par le processus
 * d'ingestion. La création, la modification et la suppression sont réservées
 * à la logique métier interne du microservice.
 */
public abstract class AbstractReadOnlyController<T extends HasId> {
    protected abstract AbstractCrudService<T> service();

    @GetMapping
    public List<T> getAll() {
        return service().findAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable String id) {
        return service().findById(id);
    }
}
