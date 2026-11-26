package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.HealthAdvice;
import tn.esprit.air_et_sante.service.IHealthAdviceService;

@Tag(name = "Conseils santé", description = "Gestion des conseils santé")
@RestController
@RequestMapping("/health-advices")
@RequiredArgsConstructor
public class HealthAdviceController {

    private static final String ID_DESC = "Identifiant du conseil santé";
    private static final String NOT_FOUND = "Conseil santé introuvable";
    private static final String INVALID = "Données invalides";

    private final IHealthAdviceService healthAdviceService;

    @Operation(summary = "Lister les conseils santé")
    @ApiResponse(responseCode = "200", description = "Liste des conseils santé")
    @GetMapping
    public List<HealthAdvice> findAll() {
        return healthAdviceService.findAll();
    }

    @Operation(summary = "Consulter un conseil santé")
    @ApiResponse(responseCode = "200", description = "Conseil santé trouvé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public HealthAdvice findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return healthAdviceService.findById(id);
    }

    @Operation(summary = "Créer un conseil santé")
    @ApiResponse(responseCode = "201", description = "Conseil santé créé")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthAdvice create(@RequestBody HealthAdvice healthAdvice) {
        return healthAdviceService.create(healthAdvice);
    }

    @Operation(summary = "Modifier un conseil santé")
    @ApiResponse(responseCode = "200", description = "Conseil santé modifié")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public HealthAdvice update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody HealthAdvice healthAdvice) {
        return healthAdviceService.update(id, healthAdvice);
    }

    @Operation(summary = "Supprimer un conseil santé")
    @ApiResponse(responseCode = "204", description = "Conseil santé supprimé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        healthAdviceService.deleteById(id);
    }
}
