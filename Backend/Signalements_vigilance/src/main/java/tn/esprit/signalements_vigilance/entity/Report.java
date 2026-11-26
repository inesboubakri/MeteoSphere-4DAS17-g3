package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.ReportStatus;
import tn.esprit.signalements_vigilance.enums.RiskType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_zone_type", columnList = "zone_id,type"),
        @Index(name = "idx_reports_user", columnList = "user_id")
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskType type;

    /** gravite de 1 (faible) a 5 (critique) */
    @Column(nullable = false)
    private Integer gravite;

    @Column(length = 2000)
    private String description;

    private Double lat;
    private Double lon;

    @Column(name = "zone_id", nullable = false, length = 50)
    private String zoneId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus statut = ReportStatus.EN_ATTENTE;

    @Column(name = "score_fiabilite", nullable = false)
    private Integer scoreFiabilite = 50;

    @Column(nullable = false)
    private Integer confirmations = 0;

    @Column(nullable = false)
    private Integer contestations = 0;

    /** id du signalement original si celui-ci est un doublon (meme zone, meme type, rayon proche) */
    @Column(name = "duplicate_of")
    private Long duplicateOf;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    @PrePersist
    void onCreate() {
        if (creeLe == null) creeLe = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public RiskType getType() { return type; }
    public void setType(RiskType type) { this.type = type; }
    public Integer getGravite() { return gravite; }
    public void setGravite(Integer gravite) { this.gravite = gravite; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    public ReportStatus getStatut() { return statut; }
    public void setStatut(ReportStatus statut) { this.statut = statut; }
    public Integer getScoreFiabilite() { return scoreFiabilite; }
    public void setScoreFiabilite(Integer scoreFiabilite) { this.scoreFiabilite = scoreFiabilite; }
    public Integer getConfirmations() { return confirmations; }
    public void setConfirmations(Integer confirmations) { this.confirmations = confirmations; }
    public Integer getContestations() { return contestations; }
    public void setContestations(Integer contestations) { this.contestations = contestations; }
    public Long getDuplicateOf() { return duplicateOf; }
    public void setDuplicateOf(Long duplicateOf) { this.duplicateOf = duplicateOf; }
    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
}
