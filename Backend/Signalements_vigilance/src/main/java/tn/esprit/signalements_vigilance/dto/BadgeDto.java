package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.Badge;
import tn.esprit.signalements_vigilance.enums.BadgeType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class BadgeDto {
    private BadgeDto() {}

    public record Request(@NotNull Long userId, @NotNull BadgeType type) {}

    public record Response(Long id, Long userId, BadgeType type, LocalDateTime obtenuLe) {
        public static Response from(Badge b) {
            return new Response(b.getId(), b.getUserId(), b.getType(), b.getObtenuLe());
        }
    }
}
