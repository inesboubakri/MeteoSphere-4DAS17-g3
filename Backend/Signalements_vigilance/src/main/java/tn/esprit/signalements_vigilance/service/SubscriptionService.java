package tn.esprit.signalements_vigilance.service;

import tn.esprit.signalements_vigilance.dto.PageResponse;
import tn.esprit.signalements_vigilance.dto.SubscriptionDto;
import tn.esprit.signalements_vigilance.entity.NotificationSubscription;
import tn.esprit.signalements_vigilance.enums.NotificationChannel;
import tn.esprit.signalements_vigilance.exception.NotFoundException;
import tn.esprit.signalements_vigilance.repository.NotificationSubscriptionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionService {

    private final NotificationSubscriptionRepository subscriptions;

    public SubscriptionService(NotificationSubscriptionRepository subscriptions) {
        this.subscriptions = subscriptions;
    }

    public SubscriptionDto.Response create(SubscriptionDto.Request req) {
        NotificationSubscription s = new NotificationSubscription();
        apply(s, req);
        return SubscriptionDto.Response.from(subscriptions.save(s));
    }

    @Transactional(readOnly = true)
    public PageResponse<SubscriptionDto.Response> list(Long userId, String zone, Boolean actif, int page, int size) {
        var result = subscriptions.search(userId, zone, actif, Pages.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        return PageResponse.of(result, SubscriptionDto.Response::from);
    }

    @Transactional(readOnly = true)
    public SubscriptionDto.Response get(Long id) {
        return SubscriptionDto.Response.from(find(id));
    }

    public SubscriptionDto.Response update(Long id, SubscriptionDto.Request req) {
        NotificationSubscription s = find(id);
        apply(s, req);
        return SubscriptionDto.Response.from(subscriptions.save(s));
    }

    public void delete(Long id) {
        subscriptions.delete(find(id));
    }

    private void apply(NotificationSubscription s, SubscriptionDto.Request req) {
        s.setUserId(req.userId());
        s.setZone(req.zone());
        s.setTypeRisque(req.typeRisque());
        s.setCanal(req.canal() != null ? req.canal() : NotificationChannel.APPLICATION);
        s.setActif(req.actif() != null ? req.actif() : Boolean.TRUE);
    }

    private NotificationSubscription find(Long id) {
        return subscriptions.findById(id).orElseThrow(() -> NotFoundException.of("Abonnement", id));
    }
}
