package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.NotificationChannel;
import tn.esprit.signalements_vigilance.enums.RiskType;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_subscriptions", indexes =
        @Index(name = "idx_sub_zone", columnList = "zone"))
public class NotificationSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String zone;

    /** null = tous les types de risque */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_risque", length = 30)
    private RiskType typeRisque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel canal = NotificationChannel.APPLICATION;

    @Column(nullable = false)
    private Boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public RiskType getTypeRisque() { return typeRisque; }
    public void setTypeRisque(RiskType typeRisque) { this.typeRisque = typeRisque; }
    public NotificationChannel getCanal() { return canal; }
    public void setCanal(NotificationChannel canal) { this.canal = canal; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
