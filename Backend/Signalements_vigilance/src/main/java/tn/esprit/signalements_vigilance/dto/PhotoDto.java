package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.entity.ReportPhoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class PhotoDto {
    private PhotoDto() {}

    public record Request(@NotBlank @Size(max = 500) String url, @Size(max = 255) String legende) {}

    public record Response(Long id, Long reportId, String url, String legende) {
        public static Response from(ReportPhoto p) {
            return new Response(p.getId(), p.getReport().getId(), p.getUrl(), p.getLegende());
        }
    }
}
