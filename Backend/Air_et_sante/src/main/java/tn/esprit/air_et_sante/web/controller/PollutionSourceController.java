package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.PollutionSource;
import tn.esprit.air_et_sante.service.IPollutionSourceService;

@Tag(name = "Sources de pollution", description = "Gestion des sources de pollution")
@RestController
@RequestMapping("/pollution-sources")
@RequiredArgsConstructor
public class PollutionSourceController {

    private static final String ID_DESC = "Identifiant de la source de pollution";
    private static final String NOT_FOUND = "Source de pollution introuvable";
    private static final String INVALID = "Données invalides";

    private final IPollutionSourceService pollutionSourceService;

    @Operation(summary = "Lister les sources de pollution")
    @ApiResponse(responseCode = "200", description = "Liste des sources de pollution")
    @GetMapping
    public List<PollutionSource> findAll() {
        return pollutionSourceService.findAll();
    }

    @Operation(summary = "Consulter une source de pollution")
    @ApiResponse(responseCode = "200", description = "Source de pollution trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public PollutionSource findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return pollutionSourceService.findById(id);
    }

    @Operation(summary = "Créer une source de pollution")
    @ApiResponse(responseCode = "201", description = "Source de pollution créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PollutionSource create(@RequestBody PollutionSource pollutionSource) {
        return pollutionSourceService.create(pollutionSource);
    }

    @Operation(summary = "Modifier une source de pollution")
    @ApiResponse(responseCode = "200", description = "Source de pollution modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public PollutionSource update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody PollutionSource pollutionSource) {
        return pollutionSourceService.update(id, pollutionSource);
    }

    @Operation(summary = "Supprimer une source de pollution")
    @ApiResponse(responseCode = "204", description = "Source de pollution supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        pollutionSourceService.deleteById(id);
    }
}
