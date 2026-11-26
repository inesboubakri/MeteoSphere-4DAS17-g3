package tn.esprit.prevision.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.prevision.domain.WeatherCondition;
import tn.esprit.prevision.service.IWeatherConditionService;

@Tag(name = "Conditions météo", description = "Gestion des conditions météorologiques")
@RestController
@RequestMapping("/weather-conditions")
@RequiredArgsConstructor
public class WeatherConditionController {

    private static final String ID_DESC = "Identifiant de la condition";
    private static final String NOT_FOUND = "Condition introuvable";
    private static final String INVALID = "Données invalides";

    private final IWeatherConditionService conditionService;

    @Operation(summary = "Lister les conditions météo")
    @ApiResponse(responseCode = "200", description = "Liste des conditions météo")
    @GetMapping
    public List<WeatherCondition> findAll() {
        return conditionService.findAll();
    }

    @Operation(summary = "Consulter une condition météo")
    @ApiResponse(responseCode = "200", description = "Condition trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public WeatherCondition findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return conditionService.findById(id);
    }

    @Operation(summary = "Créer une condition météo")
    @ApiResponse(responseCode = "201", description = "Condition créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherCondition create(@RequestBody WeatherCondition condition) {
        return conditionService.create(condition);
    }

    @Operation(summary = "Modifier une condition météo")
    @ApiResponse(responseCode = "200", description = "Condition modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public WeatherCondition update(@Parameter(description = ID_DESC) @PathVariable Long id,
                                   @RequestBody WeatherCondition condition) {
        return conditionService.update(id, condition);
    }

    @Operation(summary = "Supprimer une condition météo")
    @ApiResponse(responseCode = "204", description = "Condition supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        conditionService.deleteById(id);
    }
}
