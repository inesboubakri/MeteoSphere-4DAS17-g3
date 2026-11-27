package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.MonitoredZone;
import tn.esprit.ingestion.service.MonitoredZoneService;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitored-zones")
public class MonitoredZoneController extends AbstractCrudController<MonitoredZone> {
    private final MonitoredZoneService service;
    public MonitoredZoneController(MonitoredZoneService service) { this.service = service; }
    @Override protected AbstractCrudService<MonitoredZone> service() { return service; }
}
