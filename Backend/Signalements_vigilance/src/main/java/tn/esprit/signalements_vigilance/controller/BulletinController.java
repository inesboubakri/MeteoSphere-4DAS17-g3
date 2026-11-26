package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.BulletinDto;
import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.service.BulletinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bulletins")
@Tag(name = "Bulletins officiels", description = "Bulletins de vigilance publies par les meteorologues")
public class BulletinController {

    private final BulletinService bulletins;

    public BulletinController(BulletinService bulletins) {
        this.bulletins = bulletins;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Publier un bulletin (recalcule la vigilance de la zone)")
    public BulletinDto.Response create(@Valid @RequestBody BulletinDto.Request req) {
        return bulletins.create(req);
    }

    @GetMapping
    @Operation(summary = "Lister les bulletins (filtres zone, type ; activeOnly=true pour les bulletins en cours)")
    public PageResponse<BulletinDto.Response> list(@RequestParam(required = false) String zone,
                                                   @RequestParam(required = false) RiskType type,
                                                   @RequestParam(defaultValue = "false") boolean activeOnly,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return bulletins.list(zone, type, activeOnly, page, size);
    }

    @GetMapping("/{id}")
    public BulletinDto.Response get(@PathVariable Long id) {
        return bulletins.get(id);
    }

    @PutMapping("/{id}")
    public BulletinDto.Response update(@PathVariable Long id, @Valid @RequestBody BulletinDto.Request req) {
        return bulletins.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bulletins.delete(id);
    }
}
