package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.IngestionError;
import tn.esprit.ingestion.service.IngestionErrorService;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion-errors")
public class IngestionErrorController extends AbstractReadOnlyController<IngestionError> {
    private final IngestionErrorService service;

    public IngestionErrorController(IngestionErrorService service) {
        this.service = service;
    }

    @Override
    protected AbstractCrudService<IngestionError> service() {
        return service;
    }
}
