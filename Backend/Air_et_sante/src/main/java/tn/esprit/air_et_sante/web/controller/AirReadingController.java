package tn.esprit.air_et_sante.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.air_et_sante.domain.AirReading;
import tn.esprit.air_et_sante.service.IAirReadingService;

@Tag(name = "Relevés", description = "Gestion des relevés de qualité de l'air")
@RestController
@RequestMapping("/air-readings")
@RequiredArgsConstructor
public class AirReadingController {

    private static final String ID_DESC = "Identifiant du relevé";
    private static final String NOT_FOUND = "Relevé introuvable";
    private static final String INVALID = "Données invalides";

    private final IAirReadingService airReadingService;

    @Operation(summary = "Lister les relevés")
    @ApiResponse(responseCode = "200", description = "Liste des relevés")
    @GetMapping
    public List<AirReading> findAll() {
        return airReadingService.findAll();
    }

    @Operation(summary = "Consulter un relevé")
    @ApiResponse(responseCode = "200", description = "Relevé trouvé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public AirReading findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return airReadingService.findById(id);
    }

    @Operation(summary = "Créer un relevé")
    @ApiResponse(responseCode = "201", description = "Relevé créé")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AirReading create(@RequestBody AirReading airReading) {
        return airReadingService.create(airReading);
    }

    @Operation(summary = "Modifier un relevé")
    @ApiResponse(responseCode = "200", description = "Relevé modifié")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public AirReading update(@Parameter(description = ID_DESC) @PathVariable Long id,
                      @RequestBody AirReading airReading) {
        return airReadingService.update(id, airReading);
    }

    @Operation(summary = "Supprimer un relevé")
    @ApiResponse(responseCode = "204", description = "Relevé supprimé")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        airReadingService.deleteById(id);
    }
}
