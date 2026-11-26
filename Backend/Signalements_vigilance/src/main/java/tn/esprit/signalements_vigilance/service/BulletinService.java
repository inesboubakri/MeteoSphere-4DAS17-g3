package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.BulletinDto;
import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.entity.OfficialBulletin;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.OfficialBulletinRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class BulletinService {

    private final OfficialBulletinRepository bulletins;
    private final VigilanceService vigilance;

    public BulletinService(OfficialBulletinRepository bulletins, VigilanceService vigilance) {
        this.bulletins = bulletins;
        this.vigilance = vigilance;
    }

    public BulletinDto.Response create(BulletinDto.Request req) {
        OfficialBulletin b = new OfficialBulletin();
        apply(b, req);
        OfficialBulletin saved = bulletins.saveAndFlush(b);
        vigilance.recompute(saved.getZone(), saved.getTypeRisque());
        return BulletinDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<BulletinDto.Response> list(String zone, RiskType type, boolean activeOnly, int page, int size) {
        LocalDateTime at = activeOnly ? LocalDateTime.now() : null;
        var result = bulletins.search(zone, type, at, Pages.of(page, size, Sort.by(Sort.Direction.DESC, "valideDe")));
        return PageResponse.of(result, BulletinDto.Response::from);
    }

    @Transactional(readOnly = true)
    public BulletinDto.Response get(Long id) {
        return BulletinDto.Response.from(find(id));
    }

    public BulletinDto.Response update(Long id, BulletinDto.Request req) {
        OfficialBulletin b = find(id);
        String oldZone = b.getZone();
        RiskType oldType = b.getTypeRisque();
        apply(b, req);
        OfficialBulletin saved = bulletins.saveAndFlush(b);
        vigilance.recompute(saved.getZone(), saved.getTypeRisque());
        if (!oldZone.equals(saved.getZone()) || oldType != saved.getTypeRisque()) {
            vigilance.recompute(oldZone, oldType);
        }
        return BulletinDto.Response.from(saved);
    }

    public void delete(Long id) {
        OfficialBulletin b = find(id);
        String zone = b.getZone();
        RiskType type = b.getTypeRisque();
        bulletins.delete(b);
        bulletins.flush();
        vigilance.recompute(zone, type);
    }

    private void apply(OfficialBulletin b, BulletinDto.Request req) {
        if (!req.valideJusqua().isAfter(req.valideDe())) {
            throw new BadRequestException("valideJusqua doit etre posterieure a valideDe");
        }
        b.setMeteorologueId(req.meteorologueId());
        b.setZone(req.zone());
        b.setTypeRisque(req.typeRisque());
        b.setNiveau(req.niveau());
        b.setTexte(req.texte());
        b.setValideDe(req.valideDe());
        b.setValideJusqua(req.valideJusqua());
    }

    private OfficialBulletin find(Long id) {
        return bulletins.findById(id).orElseThrow(() -> NotFoundException.of("Bulletin", id));
    }
}
