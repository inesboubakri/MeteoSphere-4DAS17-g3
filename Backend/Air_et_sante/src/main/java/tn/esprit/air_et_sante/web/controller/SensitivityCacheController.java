package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.SensitivityCache;
import tn.esprit.air_et_sante.service.ISensitivityCacheService;

@Tag(name = "Sensibilités", description = "Gestion des sensibilités des utilisateurs")
@RestController
@RequestMapping("/sensitivities")
@RequiredArgsConstructor
public class SensitivityCacheController {

    private static final String ID_DESC = "Identifiant de la sensibilité";
    private static final String NOT_FOUND = "Sensibilité introuvable";
    private static final String INVALID = "Données invalides";

    private final ISensitivityCacheService sensitivityCacheService;

    @Operation(summary = "Lister les sensibilités")
    @ApiResponse(responseCode = "200", description = "Liste des sensibilités")
    @GetMapping
    public List<SensitivityCache> findAll() {
        return sensitivityCacheService.findAll();
    }

    @Operation(summary = "Consulter une sensibilité")
    @ApiResponse(responseCode = "200", description = "Sensibilité trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public SensitivityCache findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return sensitivityCacheService.findById(id);
    }

    @Operation(summary = "Créer une sensibilité")
    @ApiResponse(responseCode = "201", description = "Sensibilité créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensitivityCache create(@RequestBody SensitivityCache sensitivityCache) {
        return sensitivityCacheService.create(sensitivityCache);
    }

    @Operation(summary = "Modifier une sensibilité")
    @ApiResponse(responseCode = "200", description = "Sensibilité modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public SensitivityCache update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody SensitivityCache sensitivityCache) {
        return sensitivityCacheService.update(id, sensitivityCache);
    }

    @Operation(summary = "Supprimer une sensibilité")
    @ApiResponse(responseCode = "204", description = "Sensibilité supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        sensitivityCacheService.deleteById(id);
    }
}
