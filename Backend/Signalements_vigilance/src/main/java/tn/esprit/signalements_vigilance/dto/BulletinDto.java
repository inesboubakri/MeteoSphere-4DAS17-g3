package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.OfficialBulletin;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class BulletinDto {
    private BulletinDto() {}

    public record Request(
            @NotNull Long meteorologueId,
            @NotBlank @Size(max = 50) String zone,
            @NotNull RiskType typeRisque,
            @NotNull VigilanceLevel niveau,
            @NotBlank @Size(max = 2000) String texte,
            @NotNull LocalDateTime valideDe,
            @NotNull LocalDateTime valideJusqua) {}

    public record Response(Long id, Long meteorologueId, String zone, RiskType typeRisque, VigilanceLevel niveau,
                           String texte, LocalDateTime valideDe, LocalDateTime valideJusqua) {
        public static Response from(OfficialBulletin b) {
            return new Response(b.getId(), b.getMeteorologueId(), b.getZone(), b.getTypeRisque(), b.getNiveau(),
                    b.getTexte(), b.getValideDe(), b.getValideJusqua());
        }
    }
}
