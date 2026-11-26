package tn.esprit.prevision.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.prevision.domain.ForecastModel;
import tn.esprit.prevision.service.IForecastModelService;

@Tag(name = "Modèles de prévision", description = "Gestion des modèles de prévision météo")
@RestController
@RequestMapping("/forecast-models")
@RequiredArgsConstructor
public class ForecastModelController {

    private static final String ID_DESC = "Identifiant du modèle";
    private static final String NOT_FOUND = "Modèle introuvable";
    private static final String INVALID = "Données invalides";

    private final IForecastModelService modelService;

    @Operation(summary = "Lister les modèles")
    @ApiResponse(responseCode = "200", description = "Liste des modèles")
    @GetMapping
    public List<ForecastModel> findAll() {
        return modelService.findAll();
    }

    @Operation(summary = "Consulter un modèle")
    @ApiResponse(responseCode = "200", description = "Modèle trouvé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public ForecastModel findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return modelService.findById(id);
    }

    @Operation(summary = "Créer un modèle")
    @ApiResponse(responseCode = "201", description = "Modèle créé")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ForecastModel create(@RequestBody ForecastModel model) {
        return modelService.create(model);
    }

    @Operation(summary = "Modifier un modèle")
    @ApiResponse(responseCode = "200", description = "Modèle modifié")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public ForecastModel update(@Parameter(description = ID_DESC) @PathVariable Long id,
                                @RequestBody ForecastModel model) {
        return modelService.update(id, model);
    }

    @Operation(summary = "Supprimer un modèle")
    @ApiResponse(responseCode = "204", description = "Modèle supprimé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        modelService.deleteById(id);
    }
}
