package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.NotificationEvent;
import tn.esprit.signalements_vigilance.entity.NotificationSubscription;
import tn.esprit.signalements_vigilance.entity.Report;
import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import tn.esprit.signalements_vigilance.repository.NotificationSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Notifications : fonctionnalite du service (pas un service a part).
 * Declenchees quand le niveau de vigilance d'une zone change ou qu'un signalement grave est valide.
 * L'envoi est simule (log) ; les notifications sont gardees dans un journal en memoire (200 dernieres)
 * consultable via GET /notifications pour la demonstration. WebSocket / email reels : etapes suivantes.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final int MAX_JOURNAL = 200;

    private final NotificationSubscriptionRepository subscriptions;
    private final Deque<NotificationEvent> journal = new ConcurrentLinkedDeque<>();
    private final AtomicLong sequence = new AtomicLong();

    public NotificationService(NotificationSubscriptionRepository subscriptions) {
        this.subscriptions = subscriptions;
    }

    /** Le niveau de vigilance d'une zone a change. */
    public List<NotificationEvent> onLevelChanged(String zone, RiskType type, VigilanceLevel previous,
                                                  VigilanceLevel current, int index) {
        String titre = "Vigilance " + current + " - " + zone;
        String message = String.format("Zone %s, risque %s : passage de %s a %s (indice %d/100).",
                zone, type, previous, current, index);
        return dispatch(zone, type, titre, message, null);
    }

    /** Un signalement grave (gravite >= 4) vient d'etre valide dans la zone. */
    public List<NotificationEvent> onSevereReportValidated(Report report) {
        String titre = "Signalement grave valide - " + report.getZoneId();
        String message = String.format("%s (gravite %d/5) signale et valide dans la zone %s.",
                report.getType(), report.getGravite(), report.getZoneId());
        return dispatch(report.getZoneId(), report.getType(), titre, message, report.getUserId());
    }

    /** Dernieres notifications, plus recentes d'abord ; userId facultatif. */
    public List<NotificationEvent> recent(Long userId, int limit) {
        List<NotificationEvent> result = new ArrayList<>();
        var it = journal.descendingIterator();
        while (it.hasNext() && result.size() < Math.max(limit, 1)) {
            NotificationEvent e = it.next();
            if (userId == null || userId.equals(e.userId())) {
                result.add(e);
            }
        }
        return result;
    }

    public void clear() {
        journal.clear();
    }

    private List<NotificationEvent> dispatch(String zone, RiskType type, String titre, String message, Long excludedUserId) {
        List<NotificationEvent> sent = new ArrayList<>();
        for (NotificationSubscription s : subscriptions.findMatching(zone, type)) {
            if (excludedUserId != null && excludedUserId.equals(s.getUserId())) {
                continue; // l'auteur n'est pas notifie de son propre signalement
            }
            NotificationEvent event = new NotificationEvent(sequence.incrementAndGet(), LocalDateTime.now(),
                    s.getUserId(), s.getCanal(), zone, type, titre, message);
            log.info("[NOTIF {}] user={} : {} - {}", s.getCanal(), s.getUserId(), titre, message);
            journal.addLast(event);
            while (journal.size() > MAX_JOURNAL) {
                journal.pollFirst();
            }
            sent.add(event);
        }
        return sent;
    }
}
