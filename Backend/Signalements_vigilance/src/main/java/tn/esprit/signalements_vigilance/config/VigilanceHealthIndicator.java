package tn.esprit.signalements_vigilance.config;

import tn.esprit.signalements_vigilance.repository.NotificationSubscriptionRepository;
import tn.esprit.signalements_vigilance.repository.VigilanceZoneRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class VigilanceHealthIndicator implements HealthIndicator {

    private final VigilanceZoneRepository zones;
    private final NotificationSubscriptionRepository subscriptions;

    public VigilanceHealthIndicator(VigilanceZoneRepository zones, NotificationSubscriptionRepository subscriptions) {
        this.zones = zones;
        this.subscriptions = subscriptions;
    }

    @Override
    public Health health() {
        try {
            return Health.up()
                    .withDetail("zonesSuivies", zones.count())
                    .withDetail("abonnements", subscriptions.count())
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}