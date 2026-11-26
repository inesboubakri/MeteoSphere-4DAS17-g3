package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.dto.VigilanceDto;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import tn.esprit.signalements_vigilance.service.VigilanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/vigilance")
@Tag(name = "Vigilance", description = "Niveau de vigilance par zone : vert 0-39, jaune 40-59, orange 60-79, rouge 80-100")
public class VigilanceController {

    private final VigilanceService vigilance;

    public VigilanceController(VigilanceService vigilance) {
        this.vigilance = vigilance;
    }

    @PostMapping("/zones")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer une vigilance manuellement (meteorologue / admin)")
    public VigilanceDto.Response create(@Valid @RequestBody VigilanceDto.Request req) {
        return vigilance.create(req);
    }

    @GetMapping("/zones")
    @Operation(summary = "Lister les vigilances (filtres zone, type, niveau ; tri indice | recent | zone)")
    public PageResponse<VigilanceDto.Response> list(@RequestParam(required = false) String zone,
                                                    @RequestParam(required = false) RiskType type,
                                                    @RequestParam(required = false) VigilanceLevel niveau,
                                                    @RequestParam(defaultValue = "indice") String sort,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return vigilance.list(zone, type, niveau, sort, page, size);
    }

    @GetMapping("/zones/{id}")
    public VigilanceDto.Response get(@PathVariable Long id) {
        return vigilance.get(id);
    }

    @PutMapping("/zones/{id}")
    @Operation(summary = "Modifier une vigilance (notifie les abonnes si le niveau change)")
    public VigilanceDto.Response update(@PathVariable Long id, @Valid @RequestBody VigilanceDto.Request req) {
        return vigilance.update(id, req);
    }

    @DeleteMapping("/zones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vigilance.delete(id);
    }

    @PostMapping("/zones/recompute")
    @Operation(summary = "Recalculer la vigilance d'une zone",
            description = "A partir des signalements valides des dernieres 24 h et des bulletins officiels actifs.")
    public VigilanceDto.Response recompute(@RequestParam String zone, @RequestParam RiskType type) {
        return vigilance.recompute(zone, type);
    }

    @GetMapping("/map")
    @Operation(summary = "Carte de vigilance (GeoJSON FeatureCollection)")
    public Map<String, Object> map(@RequestParam(required = false) RiskType type) {
        return vigilance.map(type);
    }
}
