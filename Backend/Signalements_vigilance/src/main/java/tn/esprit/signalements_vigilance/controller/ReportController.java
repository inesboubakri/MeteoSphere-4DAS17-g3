package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.*;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.service.CommentService;
import tn.esprit.signalements_vigilance.service.PhotoService;
import tn.esprit.signalements_vigilance.service.ReportService;
import tn.esprit.signalements_vigilance.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Signalements", description = "CRUD des signalements, moderation, photos, commentaires et votes")
public class ReportController {

    private final ReportService reports;
    private final PhotoService photos;
    private final CommentService comments;
    private final VoteService votes;

    public ReportController(ReportService reports, PhotoService photos, CommentService comments, VoteService votes) {
        this.reports = reports;
        this.photos = photos;
        this.comments = comments;
        this.votes = votes;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un signalement (detection automatique des doublons)")
    public ReportDto.Response create(@Valid @RequestBody ReportDto.Request req) {
        return reports.create(req);
    }

    @GetMapping
    @Operation(summary = "Lister / filtrer / trier les signalements",
            description = "Filtres : zone, type, statut, userId, from, to (ISO-8601). Tri : recent | votes | graves | fiabilite.")
    public PageResponse<ReportDto.Response> search(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) RiskType type,
            @RequestParam(required = false) ReportStatus statut,
            @RequestParam(required = false) Long userId,
            @Parameter(description = "ex : 2026-11-25T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "ex : 2026-11-26T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return reports.search(zone, type, statut, userId, from, to, sort, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'un signalement")
    public ReportDto.Response get(@PathVariable Long id) {
        return reports.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un signalement EN_ATTENTE")
    public ReportDto.Response update(@PathVariable Long id, @Valid @RequestBody ReportDto.Request req) {
        return reports.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un signalement et ses photos, commentaires et votes")
    public void delete(@PathVariable Long id) {
        reports.delete(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Valider ou rejeter (meteorologue)",
            description = "VALIDE : badges de reputation, recalcul de la vigilance de la zone, notification si gravite >= 4.")
    public ReportDto.Response changeStatus(@PathVariable Long id, @Valid @RequestBody ReportDto.StatusRequest req) {
        return reports.changeStatus(id, req);
    }

    // ---------- photos ----------

    @PostMapping("/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter une photo")
    public PhotoDto.Response addPhoto(@PathVariable Long id, @Valid @RequestBody PhotoDto.Request req) {
        return photos.add(id, req);
    }

    @GetMapping("/{id}/photos")
    @Operation(summary = "Photos d'un signalement")
    public List<PhotoDto.Response> listPhotos(@PathVariable Long id) {
        return photos.listByReport(id);
    }

    // ---------- commentaires ----------

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Commenter (ou repondre a un commentaire via parentId)")
    public CommentDto.Response addComment(@PathVariable Long id, @Valid @RequestBody CommentDto.Request req) {
        return comments.add(id, req);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Commentaires d'un signalement")
    public List<CommentDto.Response> listComments(@PathVariable Long id) {
        return comments.listByReport(id);
    }

    // ---------- votes ----------

    @PostMapping("/{id}/votes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Confirmer ou contester (recalcule le score de fiabilite)")
    public VoteDto.Response vote(@PathVariable Long id, @Valid @RequestBody VoteDto.Request req) {
        return votes.vote(id, req);
    }

    @GetMapping("/{id}/votes")
    @Operation(summary = "Votes d'un signalement")
    public List<VoteDto.Response> listVotes(@PathVariable Long id) {
        return votes.listByReport(id);
    }
}
