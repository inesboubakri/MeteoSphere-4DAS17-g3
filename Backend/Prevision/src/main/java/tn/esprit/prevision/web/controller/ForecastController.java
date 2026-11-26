package tn.esprit.prevision.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.prevision.domain.Forecast;
import tn.esprit.prevision.service.IForecastService;

@Tag(name = "Prévisions", description = "Gestion des prévisions météorologiques")
@RestController
@RequestMapping("/forecasts")
@RequiredArgsConstructor
public class ForecastController {

    private static final String ID_DESC = "Identifiant de la prévision";
    private static final String NOT_FOUND = "Prévision introuvable";
    private static final String INVALID = "Données invalides";

    private final IForecastService forecastService;

    @Operation(summary = "Lister les prévisions")
    @ApiResponse(responseCode = "200", description = "Liste des prévisions")
    @GetMapping
    public List<Forecast> findAll() {
        return forecastService.findAll();
    }

    @Operation(summary = "Consulter une prévision")
    @ApiResponse(responseCode = "200", description = "Prévision trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public Forecast findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return forecastService.findById(id);
    }

    @Operation(summary = "Créer une prévision")
    @ApiResponse(responseCode = "201", description = "Prévision créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Forecast create(@RequestBody Forecast forecast) {
        return forecastService.create(forecast);
    }

    @Operation(summary = "Modifier une prévision")
    @ApiResponse(responseCode = "200", description = "Prévision modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public Forecast update(@Parameter(description = ID_DESC) @PathVariable Long id,
                           @RequestBody Forecast forecast) {
        return forecastService.update(id, forecast);
    }

    @Operation(summary = "Supprimer une prévision")
    @ApiResponse(responseCode = "204", description = "Prévision supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        forecastService.deleteById(id);
    }
}
