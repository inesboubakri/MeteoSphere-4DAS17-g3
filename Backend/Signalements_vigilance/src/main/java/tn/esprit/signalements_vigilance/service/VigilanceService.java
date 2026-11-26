package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.dto.VigilanceDto;
import tn.esprit.signalements_vigilance.entity.OfficialBulletin;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.entity.VigilanceZone;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.exception.ConflictException;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.OfficialBulletinRepository;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import tn.esprit.signalements_vigilance.repository.VigilanceZoneRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class VigilanceService {

    /** fenetre de calcul : signalements valides des dernieres 24 h */
    private static final long WINDOW_HOURS = 24;

    private final VigilanceZoneRepository zones;
    private final ReportRepository reports;
    private final OfficialBulletinRepository bulletins;
    private final NotificationService notifications;

    public VigilanceService(VigilanceZoneRepository zones, ReportRepository reports,
                            OfficialBulletinRepository bulletins, NotificationService notifications) {
        this.zones = zones;
        this.reports = reports;
        this.bulletins = bulletins;
        this.notifications = notifications;
    }

    // ---------- CRUD ----------

    @Transactional(readOnly = true)
    public PageResponse<VigilanceDto.Response> list(String zone, RiskType type, VigilanceLevel niveau,
                                                    String sort, int page, int size) {
        Sort s = switch (sort == null ? "indice" : sort.toLowerCase()) {
            case "indice" -> Sort.by(Sort.Direction.DESC, "indice");
            case "recent" -> Sort.by(Sort.Direction.DESC, "misAJour");
            case "zone" -> Sort.by(Sort.Direction.ASC, "zone");
            default -> throw new BadRequestException("Tri inconnu : " + sort + " (indice, recent, zone)");
        };
        return PageResponse.of(zones.search(zone, type, niveau, Pages.of(page, size, s)), VigilanceDto.Response::from);
    }

    @Transactional(readOnly = true)
    public VigilanceDto.Response get(Long id) {
        return VigilanceDto.Response.from(find(id));
    }

    public VigilanceDto.Response create(VigilanceDto.Request req) {
        if (zones.findByZoneAndTypeRisque(req.zone(), req.typeRisque()).isPresent()) {
            throw new ConflictException("Une vigilance existe deja pour " + req.zone() + " / " + req.typeRisque()
                    + " : utiliser PUT ou /vigilance/zones/recompute");
        }
        VigilanceZone v = new VigilanceZone();
        v.setZone(req.zone());
        v.setTypeRisque(req.typeRisque());
        apply(v, req);
        VigilanceZone saved = zones.save(v);
        if (saved.getNiveau() != VigilanceLevel.VERT) {
            notifications.onLevelChanged(saved.getZone(), saved.getTypeRisque(), VigilanceLevel.VERT,
                    saved.getNiveau(), saved.getIndice());
        }
        return VigilanceDto.Response.from(saved);
    }

    public VigilanceDto.Response update(Long id, VigilanceDto.Request req) {
        VigilanceZone v = find(id);
        boolean keyChanged = !v.getZone().equals(req.zone()) || v.getTypeRisque() != req.typeRisque();
        if (keyChanged && zones.findByZoneAndTypeRisque(req.zone(), req.typeRisque()).isPresent()) {
            throw new ConflictException("Une vigilance existe deja pour " + req.zone() + " / " + req.typeRisque());
        }
        VigilanceLevel previous = v.getNiveau();
        v.setZone(req.zone());
        v.setTypeRisque(req.typeRisque());
        apply(v, req);
        VigilanceZone saved = zones.saveAndFlush(v);
        if (saved.getNiveau() != previous) {
            notifications.onLevelChanged(saved.getZone(), saved.getTypeRisque(), previous,
                    saved.getNiveau(), saved.getIndice());
        }
        return VigilanceDto.Response.from(saved);
    }

    public void delete(Long id) {
        zones.delete(find(id));
    }

    // ---------- Calcul automatique de la vigilance d'une zone ----------

    /**
     * Indice (0-100) = somme, sur les signalements VALIDES des dernieres 24 h (doublons exclus),
     * de gravite x 4 x score de fiabilite. Un bulletin officiel actif impose un plancher
     * (jaune 40, orange 60, rouge 80). Si le niveau change, les abonnes sont notifies.
     */
    public VigilanceDto.Response recompute(String zone, RiskType type) {
        LocalDateTime now = LocalDateTime.now();
        List<Report> valides = reports.findByZoneIdAndTypeAndStatutAndCreeLeAfterAndDuplicateOfIsNull(
                zone, type, ReportStatus.VALIDE, now.minusHours(WINDOW_HOURS));

        double somme = valides.stream()
                .mapToDouble(r -> r.getGravite() * 4.0 * r.getScoreFiabilite() / 100.0)
                .sum();
        int index = (int) Math.min(100, Math.round(somme));

        Optional<VigilanceLevel> bulletinLevel = bulletins.findActive(zone, type, now).stream()
                .map(OfficialBulletin::getNiveau)
                .max(Comparator.naturalOrder());
        if (bulletinLevel.isPresent()) {
            index = Math.max(index, bulletinLevel.get().getMinIndex());
        }

        VigilanceLevel level = VigilanceLevel.fromIndex(index);
        String cause = valides.size() + " signalement(s) valide(s) sur " + WINDOW_HOURS + " h"
                + bulletinLevel.map(l -> " ; bulletin officiel " + l).orElse("");

        VigilanceZone v = zones.findByZoneAndTypeRisque(zone, type).orElseGet(() -> {
            VigilanceZone created = new VigilanceZone();
            created.setZone(zone);
            created.setTypeRisque(type);
            return created;
        });
        VigilanceLevel previous = v.getId() == null ? VigilanceLevel.VERT : v.getNiveau();
        v.setIndice(index);
        v.setNiveau(level);
        v.setCause(cause);
        VigilanceZone saved = zones.saveAndFlush(v);

        if (previous != level) {
            notifications.onLevelChanged(zone, type, previous, level, index);
        }
        return VigilanceDto.Response.from(saved);
    }

    // ---------- Carte de vigilance (GeoJSON) ----------

    @Transactional(readOnly = true)
    public Map<String, Object> map(RiskType type) {
        List<VigilanceZone> list = type == null ? zones.findAll() : zones.findByTypeRisque(type);
        List<Map<String, Object>> features = new ArrayList<>();
        for (VigilanceZone z : list) {
            Map<String, Object> feature = new LinkedHashMap<>();
            feature.put("type", "Feature");
            feature.put("geometry", geometryOf(z.getZone()));
            Map<String, Object> props = new LinkedHashMap<>();
            props.put("zone", z.getZone());
            props.put("typeRisque", z.getTypeRisque());
            props.put("niveau", z.getNiveau());
            props.put("indice", z.getIndice());
            props.put("misAJour", z.getMisAJour());
            props.put("cause", z.getCause());
            feature.put("properties", props);
            features.add(feature);
        }
        Map<String, Object> collection = new LinkedHashMap<>();
        collection.put("type", "FeatureCollection");
        collection.put("features", features);
        return collection;
    }

    /** Point = barycentre des signalements valides de la zone (la zone n'a pas de polygone dans ce service). */
    private Map<String, Object> geometryOf(String zone) {
        List<Object[]> rows = reports.centroid(zone, ReportStatus.VALIDE);
        if (rows.isEmpty() || rows.get(0)[0] == null || rows.get(0)[1] == null) {
            return null;
        }
        double lat = ((Number) rows.get(0)[0]).doubleValue();
        double lon = ((Number) rows.get(0)[1]).doubleValue();
        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", List.of(lon, lat)); // GeoJSON : [longitude, latitude]
        return geometry;
    }

    private void apply(VigilanceZone v, VigilanceDto.Request req) {
        v.setIndice(req.indice());
        v.setNiveau(req.niveau() != null ? req.niveau() : VigilanceLevel.fromIndex(req.indice()));
        v.setCause(req.cause());
    }

    private VigilanceZone find(Long id) {
        return zones.findById(id).orElseThrow(() -> NotFoundException.of("Zone de vigilance", id));
    }
}
