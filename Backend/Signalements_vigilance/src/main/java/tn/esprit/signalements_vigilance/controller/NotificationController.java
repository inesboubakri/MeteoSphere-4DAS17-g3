package tn.esprit.signalements_vigilance.controller;

import tn.esprit.signalements_vigilance.dto.NotificationEvent;
import tn.esprit.signalements_vigilance.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Journal des notifications envoyees (changement de vigilance, signalement grave valide)")
public class NotificationController {

    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    @Operation(summary = "Dernieres notifications envoyees (filtre userId)",
            description = "Journal en memoire (200 dernieres), vide au redemarrage du service.")
    public List<NotificationEvent> recent(@RequestParam(required = false) Long userId,
                                          @RequestParam(defaultValue = "50") int limit) {
        return notifications.recent(userId, limit);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Vider le journal (utile pour les demonstrations)")
    public void clear() {
        notifications.clear();
    }
}
