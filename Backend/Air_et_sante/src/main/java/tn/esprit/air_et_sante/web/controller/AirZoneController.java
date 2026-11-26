package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.AirZone;
import tn.esprit.air_et_sante.service.IAirZoneService;

@Tag(name = "Zones", description = "Gestion des zones surveillées")
@RestController
@RequestMapping("/air-zones")
@RequiredArgsConstructor
public class AirZoneController {

    private static final String ID_DESC = "Identifiant la zone";
    private static final String NOT_FOUND = "Zone introuvable";
    private static final String INVALID = "Données invalides";

    private final IAirZoneService airZoneService;

    @Operation(summary = "Lister les zones")
    @ApiResponse(responseCode = "200", description = "Liste des zones")
    @GetMapping
    public List<AirZone> findAll() {
        return airZoneService.findAll();
    }

    @Operation(summary = "Consulter une zone")
    @ApiResponse(responseCode = "200", description = "Zone trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public AirZone findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return airZoneService.findById(id);
    }

    @Operation(summary = "Créer une zone")
    @ApiResponse(responseCode = "201", description = "Zone créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AirZone create(@RequestBody AirZone airZone) {
        return airZoneService.create(airZone);
    }

    @Operation(summary = "Modifier une zone")
    @ApiResponse(responseCode = "200", description = "Zone modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public AirZone update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody AirZone airZone) {
        return airZoneService.update(id, airZone);
    }

    @Operation(summary = "Supprimer une zone")
    @ApiResponse(responseCode = "204", description = "Zone supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        airZoneService.deleteById(id);
    }
}
