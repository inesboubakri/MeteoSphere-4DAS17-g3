package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.IngestionJob;
import tn.esprit.ingestion.service.IngestionJobService;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion-jobs")
public class IngestionJobController extends AbstractReadOnlyController<IngestionJob> {
    private final IngestionJobService service;

    public IngestionJobController(IngestionJobService service) {
        this.service = service;
    }

    @Override
    protected AbstractCrudService<IngestionJob> service() {
        return service;
    }
}
