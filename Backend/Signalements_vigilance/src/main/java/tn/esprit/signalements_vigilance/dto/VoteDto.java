package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.Vote;
import tn.esprit.signalements_vigilance.enums.VoteValue;
import jakarta.validation.constraints.NotNull;

public final class VoteDto {
    private VoteDto() {}

    public record Request(@NotNull Long userId, @NotNull VoteValue valeur) {}

    public record UpdateRequest(@NotNull VoteValue valeur) {}

    public record Response(Long id, Long reportId, Long userId, VoteValue valeur, Integer poids) {
        public static Response from(Vote v) {
            return new Response(v.getId(), v.getReport().getId(), v.getUserId(), v.getValeur(), v.getPoids());
        }
    }
}
