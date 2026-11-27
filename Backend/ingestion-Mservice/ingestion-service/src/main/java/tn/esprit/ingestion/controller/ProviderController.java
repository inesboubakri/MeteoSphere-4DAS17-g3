package tn.esprit.ingestion.controller;

import tn.esprit.ingestion.model.Provider;
import tn.esprit.ingestion.service.ProviderService;
import tn.esprit.ingestion.service.AbstractCrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers")
public class ProviderController extends AbstractCrudController<Provider> {
    private final ProviderService service;
    public ProviderController(ProviderService service) { this.service = service; }
    @Override protected AbstractCrudService<Provider> service() { return service; }
}
