package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.StatsDto;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.repository.ReportRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final ReportRepository reports;

    public StatsService(ReportRepository reports) {
        this.reports = reports;
    }

    public StatsDto.Overview overview() {
        Map<ReportStatus, Long> parStatut = new EnumMap<>(ReportStatus.class);
        for (ReportStatus s : ReportStatus.values()) {
            parStatut.put(s, reports.countByStatut(s));
        }

        Map<RiskType, Long> parType = new EnumMap<>(RiskType.class);
        for (Object[] row : reports.countGroupedByType()) {
            parType.put((RiskType) row[0], ((Number) row[1]).longValue());
        }

        List<StatsDto.ZoneCount> zones = new ArrayList<>();
        for (Object[] row : reports.countGroupedByZone(PageRequest.of(0, 5))) {
            zones.add(new StatsDto.ZoneCount((String) row[0], ((Number) row[1]).longValue()));
        }

        List<StatsDto.Contributor> top = new ArrayList<>();
        for (Object[] row : reports.topContributors(ReportStatus.VALIDE, PageRequest.of(0, 5))) {
            top.add(new StatsDto.Contributor(((Number) row[0]).longValue(), ((Number) row[1]).longValue()));
        }

        long valides = parStatut.get(ReportStatus.VALIDE);
        long rejetes = parStatut.get(ReportStatus.REJETE);
        double taux = (valides + rejetes) == 0 ? 0.0 : Math.round(1000.0 * valides / (valides + rejetes)) / 10.0;

        return new StatsDto.Overview(reports.count(), parStatut, parType, zones, taux, top);
    }
}
