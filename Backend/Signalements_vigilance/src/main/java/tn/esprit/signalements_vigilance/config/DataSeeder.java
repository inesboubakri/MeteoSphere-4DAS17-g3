package tn.esprit.signalements_vigilance.config;

import tn.esprit.signalements_vigilance.dto.*;
import tn.esprit.signalements_vigilance.enums.*;
import tn.esprit.signalements_vigilance.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Donnees de test (script "seed" du cahier des charges) : actif si app.seed.enabled=true et base vide. */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ReportService reports;
    private final VoteService votes;
    private final CommentService comments;
    private final PhotoService photos;
    private final SubscriptionService subscriptions;
    private final BulletinService bulletins;
    private final StatsService stats;

    public DataSeeder(ReportService reports, VoteService votes, CommentService comments, PhotoService photos,
                      SubscriptionService subscriptions, BulletinService bulletins, StatsService stats) {
        this.reports = reports;
        this.votes = votes;
        this.comments = comments;
        this.photos = photos;
        this.subscriptions = subscriptions;
        this.bulletins = bulletins;
        this.stats = stats;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (stats.overview().total() > 0) {
            return; // deja rempli
        }
        // abonnements aux alertes
        subscriptions.create(new SubscriptionDto.Request(10L, "GAB-01", RiskType.CANICULE, NotificationChannel.EMAIL, true));
        subscriptions.create(new SubscriptionDto.Request(11L, "GAB-01", null, NotificationChannel.APPLICATION, true));
        subscriptions.create(new SubscriptionDto.Request(12L, "TUN-01", RiskType.INONDATION, NotificationChannel.WEBSOCKET, true));

        // signalements (le 2e est un doublon du 1er : meme zone, meme type, a ~100 m)
        var canicule = reports.create(new ReportDto.Request(13L, RiskType.CANICULE, 4,
                "42 degres a l'ombre, rues desertes", 33.8815, 10.0982, "GAB-01"));
        reports.create(new ReportDto.Request(14L, RiskType.CANICULE, 4,
                "Chaleur extreme pres du marche", 33.8820, 10.0990, "GAB-01"));
        var inondation = reports.create(new ReportDto.Request(15L, RiskType.INONDATION, 3,
                "Route inondee avenue principale", 36.8065, 10.1815, "TUN-01"));
        var brouillard = reports.create(new ReportDto.Request(16L, RiskType.BROUILLARD, 2,
                "Visibilite reduite sur l'autoroute", 34.7406, 10.7603, "SFX-01"));

        photos.add(canicule.id(), new PhotoDto.Request("https://example.com/photos/canicule-gabes.jpg", "Thermometre de rue"));
        var commentaire = comments.add(canicule.id(), new CommentDto.Request(17L, null, "Confirme, on etouffe depuis ce matin"));
        comments.add(canicule.id(), new CommentDto.Request(13L, commentaire.id(), "Merci pour la confirmation"));

        votes.vote(canicule.id(), new VoteDto.Request(17L, VoteValue.CONFIRME));
        votes.vote(canicule.id(), new VoteDto.Request(18L, VoteValue.CONFIRME));
        votes.vote(inondation.id(), new VoteDto.Request(19L, VoteValue.CONTESTE));

        // moderation par le meteorologue : valide -> vigilance + notifications
        bulletins.create(new BulletinDto.Request(2L, "GAB-01", RiskType.CANICULE, VigilanceLevel.JAUNE,
                "Vigilance jaune canicule : temperatures superieures a 40 degres attendues.",
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(24)));
        reports.changeStatus(canicule.id(), new ReportDto.StatusRequest(ReportStatus.VALIDE));
        reports.changeStatus(brouillard.id(), new ReportDto.StatusRequest(ReportStatus.REJETE));

        log.info("Donnees de demonstration chargees (signalements, votes, abonnements, bulletin, vigilance).");
    }
}
