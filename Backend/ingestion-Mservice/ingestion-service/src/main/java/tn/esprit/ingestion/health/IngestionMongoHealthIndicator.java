package tn.esprit.ingestion.health;

import org.bson.Document;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Vérifie explicitement que la base MongoDB utilisée par Ingestion répond.
 * Le résultat est agrégé dans /actuator/health et donc communiqué à Eureka.
 */
@Component
public class IngestionMongoHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    public IngestionMongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            Object ok = result.get("ok");
            if (ok instanceof Number && ((Number) ok).doubleValue() == 1.0) {
                return Health.up()
                        .withDetail("database", "MongoDB")
                        .withDetail("check", "ping réussi")
                        .build();
            }
            return Health.down()
                    .withDetail("database", "MongoDB")
                    .withDetail("check", "réponse ping inattendue")
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("database", "MongoDB")
                    .withDetail("check", "connexion impossible")
                    .build();
        }
    }
}
