package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.dto.ReportDto;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.exception.ConflictException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReportService {

    /** rayon de detection des doublons (meme zone, meme type, dernieres 24 h) */
    private static final double DUPLICATE_RADIUS_KM = 1.0;
    private static final int SEVERE_THRESHOLD = 4;

    private final ReportRepository reports;
    private final ReportPhotoRepository photos;
    private final CommentRepository comments;
    private final VoteRepository votes;
    private final BadgeService badges;
    private final VigilanceService vigilance;
    private final NotificationService notifications;

    public ReportService(ReportRepository reports, ReportPhotoRepository photos, CommentRepository comments,
                         VoteRepository votes, BadgeService badges, VigilanceService vigilance,
                         NotificationService notifications) {
        this.reports = reports;
        this.photos = photos;
        this.comments = comments;
        this.votes = votes;
        this.badges = badges;
        this.vigilance = vigilance;
        this.notifications = notifications;
    }

    // ---------- CRUD ----------

    public ReportDto.Response create(ReportDto.Request req) {
        Report r = new Report();
        apply(r, req);
        r.setStatut(ReportStatus.EN_ATTENTE);
        r.setScoreFiabilite(badges.isTrusted(req.userId()) ? 60 : 50);
        detectDuplicate(r);
        return ReportDto.Response.from(reports.save(r));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportDto.Response> search(String zone, RiskType type, ReportStatus statut, Long userId,
                                                   LocalDateTime from, LocalDateTime to,
                                                   String sort, int page, int size) {
        var result = reports.search(zone, type, statut, userId, from, to, Pages.of(page, size, sortFor(sort)));
        return PageResponse.of(result, ReportDto.Response::from);
    }

    @Transactional(readOnly = true)
    public ReportDto.Response get(Long id) {
        return ReportDto.Response.from(find(id));
    }

    public ReportDto.Response update(Long id, ReportDto.Request req) {
        Report r = find(id);
        if (r.getStatut() != ReportStatus.EN_ATTENTE) {
            throw new ConflictException("Seul un signalement EN_ATTENTE peut etre modifie (statut actuel : " + r.getStatut() + ")");
        }
        apply(r, req);
        return ReportDto.Response.from(reports.save(r));
    }

    public void delete(Long id) {
        Report r = find(id);
        boolean wasValid = r.getStatut() == ReportStatus.VALIDE;
        String zone = r.getZoneId();
        RiskType type = r.getType();

        comments.deleteRepliesByReportId(id);
        comments.deleteRootsByReportId(id);
        photos.deleteByReportId(id);
        votes.deleteByReportId(id);
        reports.releaseDuplicates(id);
        reports.deleteById(id);

        if (wasValid) {
            vigilance.recompute(zone, type);
        }
    }

    // ---------- Moderation par le meteorologue ----------

    public ReportDto.Response changeStatus(Long id, ReportDto.StatusRequest req) {
        Report r = find(id);
        ReportStatus old = r.getStatut();
        ReportStatus now = req.statut();
        if (old == now) {
            return ReportDto.Response.from(r);
        }
        r.setStatut(now);
        reports.saveAndFlush(r);

        if (now == ReportStatus.VALIDE) {
            badges.awardForValidatedReports(r.getUserId());
        }
        if (now == ReportStatus.VALIDE || old == ReportStatus.VALIDE) {
            vigilance.recompute(r.getZoneId(), r.getType());
        }
        if (now == ReportStatus.VALIDE && r.getGravite() >= SEVERE_THRESHOLD && r.getDuplicateOf() == null) {
            notifications.onSevereReportValidated(r);
        }
        return ReportDto.Response.from(r);
    }

    // ---------- Detection de doublons ----------

    private void detectDuplicate(Report candidate) {
        if (candidate.getLat() == null || candidate.getLon() == null) {
            return;
        }
        List<Report> recents = reports.findByZoneIdAndTypeAndCreeLeAfterAndStatutNotOrderByIdAsc(
                candidate.getZoneId(), candidate.getType(), LocalDateTime.now().minusHours(24), ReportStatus.REJETE);
        for (Report other : recents) {
            if (other.getLat() == null || other.getLon() == null) continue;
            double km = haversineKm(candidate.getLat(), candidate.getLon(), other.getLat(), other.getLon());
            if (km <= DUPLICATE_RADIUS_KM) {
                Long originalId = other.getDuplicateOf() != null ? other.getDuplicateOf() : other.getId();
                candidate.setDuplicateOf(originalId);
                // une corroboration independante renforce la fiabilite de l'original
                reports.findById(originalId).ifPresent(o ->
                        o.setScoreFiabilite(Math.min(100, o.getScoreFiabilite() + 5)));
                return;
            }
        }
    }

    static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * r * Math.asin(Math.sqrt(a));
    }

    // ---------- Utilitaires ----------

    private Sort sortFor(String sort) {
        Sort recent = Sort.by(Sort.Direction.DESC, "creeLe");
        return switch (sort == null ? "recent" : sort.toLowerCase()) {
            case "recent" -> recent;
            case "votes" -> Sort.by(Sort.Direction.DESC, "confirmations").and(recent);
            case "graves", "gravite" -> Sort.by(Sort.Direction.DESC, "gravite").and(recent);
            case "fiabilite" -> Sort.by(Sort.Direction.DESC, "scoreFiabilite").and(recent);
            default -> throw new BadRequestException("Tri inconnu : " + sort + " (recent, votes, graves, fiabilite)");
        };
    }

    private void apply(Report r, ReportDto.Request req) {
        r.setUserId(req.userId());
        r.setType(req.type());
        r.setGravite(req.gravite());
        r.setDescription(req.description());
        r.setLat(req.lat());
        r.setLon(req.lon());
        r.setZoneId(req.zoneId());
    }

    private Report find(Long id) {
        return reports.findById(id).orElseThrow(() -> NotFoundException.of("Signalement", id));
    }
}
