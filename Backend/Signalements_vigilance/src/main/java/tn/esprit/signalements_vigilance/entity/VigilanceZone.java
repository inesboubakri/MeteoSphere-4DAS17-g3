package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vigilance_zones", uniqueConstraints =
        @UniqueConstraint(name = "uk_vigilance_zone_type", columnNames = {"zone", "type_risque"}))
public class VigilanceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_risque", nullable = false, length = 30)
    private RiskType typeRisque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VigilanceLevel niveau = VigilanceLevel.VERT;

    /** indice de 0 a 100 */
    @Column(nullable = false)
    private Integer indice = 0;

    @Column(name = "mis_a_jour", nullable = false)
    private LocalDateTime misAJour;

    @Column(length = 500)
    private String cause;

    @PrePersist
    @PreUpdate
    void touch() {
        misAJour = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public RiskType getTypeRisque() { return typeRisque; }
    public void setTypeRisque(RiskType typeRisque) { this.typeRisque = typeRisque; }
    public VigilanceLevel getNiveau() { return niveau; }
    public void setNiveau(VigilanceLevel niveau) { this.niveau = niveau; }
    public Integer getIndice() { return indice; }
    public void setIndice(Integer indice) { this.indice = indice; }
    public LocalDateTime getMisAJour() { return misAJour; }
    public void setMisAJour(LocalDateTime misAJour) { this.misAJour = misAJour; }
    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }
}
