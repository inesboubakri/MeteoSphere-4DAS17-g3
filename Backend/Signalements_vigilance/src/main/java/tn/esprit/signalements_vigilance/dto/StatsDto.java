package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;

import java.util.List;
import java.util.Map;

public final class StatsDto {
    private StatsDto() {}

    public record Contributor(Long userId, long signalementsValides) {}

    public record ZoneCount(String zone, long signalements) {}

    public record Overview(long total,
                           Map<ReportStatus, Long> parStatut,
                           Map<RiskType, Long> parType,
                           List<ZoneCount> zonesLesPlusTouchees,
                           double tauxValidation,
                           List<Contributor> contributeursLesPlusFiables) {}
}
