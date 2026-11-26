package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.StatsDto;
import tn.esprit.signalements_vigilance.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@Tag(name = "Statistiques")
public class StatsController {

    private final StatsService stats;

    public StatsController(StatsService stats) {
        this.stats = stats;
    }

    @GetMapping("/reports")
    @Operation(summary = "Signalements par statut/type/zone, taux de validation, contributeurs les plus fiables")
    public StatsDto.Overview overview() {
        return stats.overview();
    }
}
