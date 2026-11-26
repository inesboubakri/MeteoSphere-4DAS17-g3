package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.RiskType;
import tn.esprit.signalements_vigilance.enums.VigilanceLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "official_bulletins")
public class OfficialBulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meteorologue_id", nullable = false)
    private Long meteorologueId;

    @Column(nullable = false, length = 50)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_risque", nullable = false, length = 30)
    private RiskType typeRisque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VigilanceLevel niveau;

    @Column(nullable = false, length = 2000)
    private String texte;

    @Column(name = "valide_de", nullable = false)
    private LocalDateTime valideDe;

    @Column(name = "valide_jusqua", nullable = false)
    private LocalDateTime valideJusqua;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeteorologueId() { return meteorologueId; }
    public void setMeteorologueId(Long meteorologueId) { this.meteorologueId = meteorologueId; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public RiskType getTypeRisque() { return typeRisque; }
    public void setTypeRisque(RiskType typeRisque) { this.typeRisque = typeRisque; }
    public VigilanceLevel getNiveau() { return niveau; }
    public void setNiveau(VigilanceLevel niveau) { this.niveau = niveau; }
    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }
    public LocalDateTime getValideDe() { return valideDe; }
    public void setValideDe(LocalDateTime valideDe) { this.valideDe = valideDe; }
    public LocalDateTime getValideJusqua() { return valideJusqua; }
    public void setValideJusqua(LocalDateTime valideJusqua) { this.valideJusqua = valideJusqua; }
}
