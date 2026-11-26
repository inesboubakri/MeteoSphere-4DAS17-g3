package tn.esprit.prevision.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.prevision.domain.City;
import tn.esprit.prevision.service.ICityService;

@Tag(name = "Villes", description = "Gestion des villes pour les prévisions")
@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private static final String ID_DESC = "Identifiant de la ville";
    private static final String NOT_FOUND = "Ville introuvable";
    private static final String INVALID = "Données invalides";

    private final ICityService cityService;

    @Operation(summary = "Lister les villes")
    @ApiResponse(responseCode = "200", description = "Liste des villes")
    @GetMapping
    public List<City> findAll() {
        return cityService.findAll();
    }

    @Operation(summary = "Consulter une ville")
    @ApiResponse(responseCode = "200", description = "Ville trouvée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @GetMapping("/{id}")
    public City findById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        return cityService.findById(id);
    }

    @Operation(summary = "Créer une ville")
    @ApiResponse(responseCode = "201", description = "Ville créée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public City create(@RequestBody City city) {
        return cityService.create(city);
    }

    @Operation(summary = "Modifier une ville")
    @ApiResponse(responseCode = "200", description = "Ville modifiée")
    @ApiResponse(responseCode = "400", description = INVALID)
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @PutMapping("/{id}")
    public City update(@Parameter(description = ID_DESC) @PathVariable Long id,
                       @RequestBody City city) {
        return cityService.update(id, city);
    }

    @Operation(summary = "Supprimer une ville")
    @ApiResponse(responseCode = "204", description = "Ville supprimée")
    @ApiResponse(responseCode = "404", description = NOT_FOUND)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = ID_DESC) @PathVariable Long id) {
        cityService.deleteById(id);
    }
}
