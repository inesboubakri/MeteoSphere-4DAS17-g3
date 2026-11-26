package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.BadgeDto;
import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.entity.Badge;
import tn.esprit.signalements_vigilance.enums.BadgeType;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.exception.ConflictException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.BadgeRepository;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BadgeService {

    private final BadgeRepository badges;
    private final ReportRepository reports;

    public BadgeService(BadgeRepository badges, ReportRepository reports) {
        this.badges = badges;
        this.reports = reports;
    }

    // ---------- CRUD ----------

    @Transactional(readOnly = true)
    public PageResponse<BadgeDto.Response> list(Long userId, BadgeType type, int page, int size) {
        var result = badges.search(userId, type, Pages.of(page, size, Sort.by(Sort.Direction.DESC, "obtenuLe")));
        return PageResponse.of(result, BadgeDto.Response::from);
    }

    @Transactional(readOnly = true)
    public BadgeDto.Response get(Long id) {
        return BadgeDto.Response.from(find(id));
    }

    public BadgeDto.Response create(BadgeDto.Request req) {
        if (badges.existsByUserIdAndType(req.userId(), req.type())) {
            throw new ConflictException("Cet utilisateur possede deja le badge " + req.type());
        }
        Badge b = new Badge();
        b.setUserId(req.userId());
        b.setType(req.type());
        return BadgeDto.Response.from(badges.save(b));
    }

    public BadgeDto.Response update(Long id, BadgeDto.Request req) {
        Badge b = find(id);
        boolean changed = !b.getUserId().equals(req.userId()) || b.getType() != req.type();
        if (changed && badges.existsByUserIdAndType(req.userId(), req.type())) {
            throw new ConflictException("Cet utilisateur possede deja le badge " + req.type());
        }
        b.setUserId(req.userId());
        b.setType(req.type());
        return BadgeDto.Response.from(badges.save(b));
    }

    public void delete(Long id) {
        badges.delete(find(id));
    }

    // ---------- Logique metier ----------

    /** Attribue les badges de reputation selon le nombre de signalements valides de l'utilisateur. */
    public void awardForValidatedReports(Long userId) {
        long valides = reports.countByUserIdAndStatut(userId, ReportStatus.VALIDE);
        if (valides >= 1) award(userId, BadgeType.PREMIER_SIGNALEMENT);
        if (valides >= 5) award(userId, BadgeType.CONTRIBUTEUR_FIABLE);
        if (valides >= 10) award(userId, BadgeType.SENTINELLE);
    }

    /** Contributeur de confiance : ses votes pesent double et ses signalements demarrent avec un meilleur score. */
    @Transactional(readOnly = true)
    public boolean isTrusted(Long userId) {
        return badges.existsByUserIdAndType(userId, BadgeType.CONTRIBUTEUR_FIABLE);
    }

    private void award(Long userId, BadgeType type) {
        if (!badges.existsByUserIdAndType(userId, type)) {
            Badge b = new Badge();
            b.setUserId(userId);
            b.setType(type);
            badges.save(b);
        }
    }

    private Badge find(Long id) {
        return badges.findById(id).orElseThrow(() -> NotFoundException.of("Badge", id));
    }
}
