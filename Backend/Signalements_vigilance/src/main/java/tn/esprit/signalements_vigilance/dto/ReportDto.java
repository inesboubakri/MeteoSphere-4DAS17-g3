package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public final class ReportDto {
    private ReportDto() {}

    public record Request(
            @NotNull Long userId,
            @NotNull RiskType type,
            @NotNull @Min(1) @Max(5) Integer gravite,
            @Size(max = 2000) String description,
            @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @DecimalMin("-180.0") @DecimalMax("180.0") Double lon,
            @NotBlank @Size(max = 50) String zoneId) {}

    public record StatusRequest(@NotNull ReportStatus statut) {}

    public record Response(Long id, Long userId, RiskType type, Integer gravite, String description,
                           Double lat, Double lon, String zoneId, ReportStatus statut,
                           Integer scoreFiabilite, Integer confirmations, Integer contestations,
                           Long duplicateOf, LocalDateTime creeLe) {
        public static Response from(Report r) {
            return new Response(r.getId(), r.getUserId(), r.getType(), r.getGravite(), r.getDescription(),
                    r.getLat(), r.getLon(), r.getZoneId(), r.getStatut(), r.getScoreFiabilite(),
                    r.getConfirmations(), r.getContestations(), r.getDuplicateOf(), r.getCreeLe());
        }
    }
}
