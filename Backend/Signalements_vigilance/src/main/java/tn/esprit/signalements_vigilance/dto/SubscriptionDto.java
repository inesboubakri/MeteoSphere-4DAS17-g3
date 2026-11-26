package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.NotificationSubscription;
import tn.esprit.signalements_vigilance.enums.NotificationChannel;
import tn.esprit.signalements_vigilance.enums.RiskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SubscriptionDto {
    private SubscriptionDto() {}

    /** typeRisque null = tous les types ; canal par defaut APPLICATION ; actif par defaut true */
    public record Request(
            @NotNull Long userId,
            @NotBlank @Size(max = 50) String zone,
            RiskType typeRisque,
            NotificationChannel canal,
            Boolean actif) {}

    public record Response(Long id, Long userId, String zone, RiskType typeRisque,
                           NotificationChannel canal, Boolean actif) {
        public static Response from(NotificationSubscription s) {
            return new Response(s.getId(), s.getUserId(), s.getZone(), s.getTypeRisque(), s.getCanal(), s.getActif());
        }
    }
}
