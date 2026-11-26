package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.Pollutant;
import tn.esprit.air_et_sante.service.IPollutantService;

@Tag(name = "Polluants", description = "Gestion du catalogue des polluants")
@RestController
@RequestMapping("/pollutants")
@RequiredArgsConstructor
public class PollutantController {

    private static final String ID_DESC = "Identifiant du polluant";
    private static final String NOT_FOUND = "Polluant introuvable";
    private static final String INVALID = "Données invalides";

    private final IPollutantService pollutantService;

    @Operation(summary = "Lister les polluants")
    @ApiResponse(responseCode = "200", description = "Liste des polluants")
    @GetMapping
    public List<Pollutant> findAll() {
        return pollutantService.findAll();
    }

    @Operation(summary = "Consulter un polluant")
    @ApiResponse(responseCode = "200", description = "Polluant trouvé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public Pollutant findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return pollutantService.findById(id);
    }

    @Operation(summary = "Créer un polluant")
    @ApiResponse(responseCode = "201", description = "Polluant créé")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pollutant create(@RequestBody Pollutant pollutant) {
        return pollutantService.create(pollutant);
    }

    @Operation(summary = "Modifier un polluant")
    @ApiResponse(responseCode = "200", description = "Polluant modifié")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public Pollutant update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody Pollutant pollutant) {
        return pollutantService.update(id, pollutant);
    }

    @Operation(summary = "Supprimer un polluant")
    @ApiResponse(responseCode = "204", description = "Polluant supprimé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        pollutantService.deleteById(id);
    }
}
