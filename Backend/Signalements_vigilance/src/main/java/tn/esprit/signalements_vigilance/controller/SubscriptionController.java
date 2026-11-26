package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.dto.SubscriptionDto;
import tn.esprit.signalements_vigilance.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@Tag(name = "Abonnements", description = "Abonnements aux alertes par zone, type de risque et canal")
public class SubscriptionController {

    private final SubscriptionService subscriptions;

    public SubscriptionController(SubscriptionService subscriptions) {
        this.subscriptions = subscriptions;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "S'abonner aux alertes d'une zone")
    public SubscriptionDto.Response create(@Valid @RequestBody SubscriptionDto.Request req) {
        return subscriptions.create(req);
    }

    @GetMapping
    @Operation(summary = "Lister les abonnements (filtres userId, zone, actif)")
    public PageResponse<SubscriptionDto.Response> list(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) String zone,
                                                       @RequestParam(required = false) Boolean actif,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return subscriptions.list(userId, zone, actif, page, size);
    }

    @GetMapping("/{id}")
    public SubscriptionDto.Response get(@PathVariable Long id) {
        return subscriptions.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un abonnement (ex : actif=false pour le suspendre)")
    public SubscriptionDto.Response update(@PathVariable Long id, @Valid @RequestBody SubscriptionDto.Request req) {
        return subscriptions.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        subscriptions.delete(id);
    }
}
