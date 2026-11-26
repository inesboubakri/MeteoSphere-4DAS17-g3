package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.VigilanceZone;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public final class VigilanceDto {
    private VigilanceDto() {}

    /** niveau facultatif : s'il est absent, il est deduit de l'indice (0-39 vert, 40-59 jaune, 60-79 orange, 80-100 rouge) */
    public record Request(
            @NotBlank @Size(max = 50) String zone,
            @NotNull RiskType typeRisque,
            VigilanceLevel niveau,
            @NotNull @Min(0) @Max(100) Integer indice,
            @Size(max = 500) String cause) {}

    public record Response(Long id, String zone, RiskType typeRisque, VigilanceLevel niveau,
                           Integer indice, LocalDateTime misAJour, String cause) {
        public static Response from(VigilanceZone v) {
            return new Response(v.getId(), v.getZone(), v.getTypeRisque(), v.getNiveau(),
                    v.getIndice(), v.getMisAJour(), v.getCause());
        }
    }
}
