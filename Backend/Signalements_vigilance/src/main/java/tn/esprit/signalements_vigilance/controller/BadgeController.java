package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.BadgeDto;
import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.enums.BadgeType;
import tn.esprit.signalements_vigilance.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/badges")
@Tag(name = "Badges", description = "Reputation des contributeurs (attribues automatiquement a la validation des signalements)")
public class BadgeController {

    private final BadgeService badges;

    public BadgeController(BadgeService badges) {
        this.badges = badges;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Attribuer un badge manuellement (admin)")
    public BadgeDto.Response create(@Valid @RequestBody BadgeDto.Request req) {
        return badges.create(req);
    }

    @GetMapping
    @Operation(summary = "Lister les badges (filtres userId, type)")
    public PageResponse<BadgeDto.Response> list(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) BadgeType type,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return badges.list(userId, type, page, size);
    }

    @GetMapping("/{id}")
    public BadgeDto.Response get(@PathVariable Long id) {
        return badges.get(id);
    }

    @PutMapping("/{id}")
    public BadgeDto.Response update(@PathVariable Long id, @Valid @RequestBody BadgeDto.Request req) {
        return badges.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        badges.delete(id);
    }
}
