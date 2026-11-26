package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class CommentDto {
    private CommentDto() {}

    /** parentId : id d'un commentaire racine du meme signalement (reponse), sinon null */
    public record Request(@NotNull Long userId, Long parentId, @NotBlank @Size(max = 2000) String texte) {}

    public record UpdateRequest(@NotBlank @Size(max = 2000) String texte) {}

    public record Response(Long id, Long reportId, Long userId, Long parentId, String texte, LocalDateTime creeLe) {
        public static Response from(Comment c) {
            return new Response(c.getId(), c.getReport().getId(), c.getUserId(),
                    c.getParent() == null ? null : c.getParent().getId(), c.getTexte(), c.getCreeLe());
        }
    }
}
