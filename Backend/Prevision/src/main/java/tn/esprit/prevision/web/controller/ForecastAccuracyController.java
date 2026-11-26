package tn.esprit.prevision.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.prevision.domain.ForecastAccuracy;
import tn.esprit.prevision.service.IForecastAccuracyService;

@Tag(name = "Précision des modèles", description = "Gestion des métriques de précision des modèles")
@RestController
@RequestMapping("/forecast-accuracies")
@RequiredArgsConstructor
public class ForecastAccuracyController {

    private static final String ID_DESC = "Identifiant de la mesure de précision";
    private static final String NOT_FOUND = "Mesure introuvable";
    private static final String INVALID = "Données invalides";

    private final IForecastAccuracyService accuracyService;

    @Operation(summary = "Lister les mesures de précision")
    @ApiResponse(responseCode = "200", description = "Liste des mesures")
    @GetMapping
    public List<ForecastAccuracy> findAll() {
        return accuracyService.findAll();
    }

    @Operation(summary = "Consulter une mesure de précision")
    @ApiResponse(responseCode = "200", description = "Mesure trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public ForecastAccuracy findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return accuracyService.findById(id);
    }

    @Operation(summary = "Créer une mesure de précision")
    @ApiResponse(responseCode = "201", description = "Mesure créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ForecastAccuracy create(@RequestBody ForecastAccuracy accuracy) {
        return accuracyService.create(accuracy);
    }

    @Operation(summary = "Modifier une mesure de précision")
    @ApiResponse(responseCode = "200", description = "Mesure modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public ForecastAccuracy update(@Parameter(description = ID_DESC) @PathVariable Long id,
                                   @RequestBody ForecastAccuracy accuracy) {
        return accuracyService.update(id, accuracy);
    }

    @Operation(summary = "Supprimer une mesure de précision")
    @ApiResponse(responseCode = "204", description = "Mesure supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        accuracyService.deleteById(id);
    }
}
