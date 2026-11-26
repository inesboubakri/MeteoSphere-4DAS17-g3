package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.VoteDto;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.entity.Vote;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.VoteValue;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.exception.ConflictException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import tn.esprit.signalements_vigilance.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VoteService {

    private static final int POINTS_PAR_VOTE = 10;
    private static final int POINTS_PAR_DOUBLON = 5;

    private final VoteRepository votes;
    private final ReportRepository reports;
    private final BadgeService badges;
    private final VigilanceService vigilance;

    public VoteService(VoteRepository votes, ReportRepository reports, BadgeService badges, VigilanceService vigilance) {
        this.votes = votes;
        this.reports = reports;
        this.badges = badges;
        this.vigilance = vigilance;
    }

    /** Confirme ou conteste un signalement. Un utilisateur a un seul vote par signalement : un nouveau vote remplace l'ancien. */
    public VoteDto.Response vote(Long reportId, VoteDto.Request req) {
        Report report = reports.findById(reportId).orElseThrow(() -> NotFoundException.of("Signalement", reportId));
        if (report.getStatut() == ReportStatus.REJETE) {
            throw new ConflictException("Ce signalement a ete rejete : le vote est ferme");
        }
        if (report.getUserId().equals(req.userId())) {
            throw new BadRequestException("Un utilisateur ne peut pas voter sur son propre signalement");
        }
        Vote v = votes.findByReportIdAndUserId(reportId, req.userId()).orElseGet(Vote::new);
        v.setReport(report);
        v.setUserId(req.userId());
        v.setValeur(req.valeur());
        v.setPoids(badges.isTrusted(req.userId()) ? 2 : 1);
        Vote saved = votes.saveAndFlush(v);
        refreshScore(report);
        return VoteDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public List<VoteDto.Response> listByReport(Long reportId) {
        if (!reports.existsById(reportId)) throw NotFoundException.of("Signalement", reportId);
        return votes.findByReportIdOrderByIdAsc(reportId).stream().map(VoteDto.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public VoteDto.Response get(Long id) {
        return VoteDto.Response.from(find(id));
    }

    public VoteDto.Response update(Long id, VoteDto.UpdateRequest req) {
        Vote v = find(id);
        v.setValeur(req.valeur());
        Vote saved = votes.saveAndFlush(v);
        refreshScore(saved.getReport());
        return VoteDto.Response.from(saved);
    }

    public void delete(Long id) {
        Vote v = find(id);
        Report report = v.getReport();
        votes.delete(v);
        votes.flush();
        refreshScore(report);
    }

    /**
     * Score de fiabilite (0-100) = base (50, ou 60 si l'auteur est un contributeur de confiance)
     * + 5 par doublon independant + 10 par point de confirmation - 10 par point de contestation
     * (le vote d'un contributeur de confiance compte double).
     * Si le signalement est deja valide, la vigilance de la zone est recalculee.
     */
    private void refreshScore(Report report) {
        Long id = report.getId();
        long poidsConfirme = votes.sumPoids(id, VoteValue.CONFIRME);
        long poidsConteste = votes.sumPoids(id, VoteValue.CONTESTE);
        int base = badges.isTrusted(report.getUserId()) ? 60 : 50;
        long doublons = reports.countByDuplicateOf(id);
        int score = (int) Math.max(0, Math.min(100,
                base + POINTS_PAR_DOUBLON * doublons + POINTS_PAR_VOTE * (poidsConfirme - poidsConteste)));

        report.setScoreFiabilite(score);
        report.setConfirmations((int) votes.countByReportIdAndValeur(id, VoteValue.CONFIRME));
        report.setContestations((int) votes.countByReportIdAndValeur(id, VoteValue.CONTESTE));
        reports.saveAndFlush(report);

        if (report.getStatut() == ReportStatus.VALIDE) {
            vigilance.recompute(report.getZoneId(), report.getType());
        }
    }

    private Vote find(Long id) {
        return votes.findById(id).orElseThrow(() -> NotFoundException.of("Vote", id));
    }
}
