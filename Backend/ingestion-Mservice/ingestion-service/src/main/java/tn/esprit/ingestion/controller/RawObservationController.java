package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.RawObservation;
import tn.esprit.ingestion.service.RawObservationService;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/raw-observations")
public class RawObservationController extends AbstractReadOnlyController<RawObservation> {
    private final RawObservationService service;

    public RawObservationController(RawObservationService service) {
        this.service = service;
    }

    @Override
    protected AbstractCrudService<RawObservation> service() {
        return service;
    }
}
