package tn.esprit.signalements_vigilance;

import tn.esprit.signalements_vigilance.dto.*;
import tn.esprit.signalements_vigilance.enums.*;
import tn.esprit.signalements_vigilance.exception.BadRequestException;
import tn.esprit.signalements_vigilance.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class SignalementsVigilanceApplicationTests {

    @Autowired ReportService reports;
    @Autowired VoteService votes;
    @Autowired SubscriptionService subscriptions;
    @Autowired BulletinService bulletins;
    @Autowired VigilanceService vigilance;
    @Autowired NotificationService notifications;
    @Autowired StatsService stats;

    @Test
    void contextLoads() {
    }

    /** Scenario complet : signalement -> doublon -> vote -> validation -> bulletin -> vigilance -> notification. */
    @Test
    void scenarioSignalementVigilanceNotification() {
        subscriptions.create(new SubscriptionDto.Request(100L, "TST-01", RiskType.CANICULE, NotificationChannel.APPLICATION, true));

        var r1 = reports.create(new ReportDto.Request(1L, RiskType.CANICULE, 5, "Tres chaud", 33.88, 10.09, "TST-01"));
        assertThat(r1.statut()).isEqualTo(ReportStatus.EN_ATTENTE);
        assertThat(r1.scoreFiabilite()).isEqualTo(50);
        assertThat(r1.duplicateOf()).isNull();

        // meme zone, meme type, ~100 m : doublon de r1
        var r2 = reports.create(new ReportDto.Request(2L, RiskType.CANICULE, 4, "Chaleur", 33.8805, 10.0905, "TST-01"));
        assertThat(r2.duplicateOf()).isEqualTo(r1.id());

        // vote : +10 de fiabilite ; interdit sur son propre signalement
        votes.vote(r1.id(), new VoteDto.Request(3L, VoteValue.CONFIRME));
        assertThrows(BadRequestException.class,
                () -> votes.vote(r1.id(), new VoteDto.Request(1L, VoteValue.CONFIRME)));
        var apresVote = reports.get(r1.id());
        assertThat(apresVote.confirmations()).isEqualTo(1);
        assertThat(apresVote.scoreFiabilite()).isGreaterThanOrEqualTo(60);

        // validation d'un signalement grave -> notification a l'abonne
        reports.changeStatus(r1.id(), new ReportDto.StatusRequest(ReportStatus.VALIDE));
        assertThat(notifications.recent(100L, 10)).isNotEmpty();

        // bulletin officiel orange -> la vigilance passe a orange (indice >= 60)
        bulletins.create(new BulletinDto.Request(9L, "TST-01", RiskType.CANICULE, VigilanceLevel.ORANGE,
                "Vigilance orange canicule", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(12)));
        var v = vigilance.recompute("TST-01", RiskType.CANICULE);
        assertThat(v.niveau()).isEqualTo(VigilanceLevel.ORANGE);
        assertThat(v.indice()).isGreaterThanOrEqualTo(60);
        assertThat(notifications.recent(100L, 10).size()).isGreaterThanOrEqualTo(2);

        assertThat(stats.overview().total()).isGreaterThanOrEqualTo(2);
        assertThat(vigilance.map(RiskType.CANICULE).get("type")).isEqualTo("FeatureCollection");
    }
}
