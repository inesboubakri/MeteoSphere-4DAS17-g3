package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.VoteValue;
import jakarta.persistence.*;

@Entity
@Table(name = "votes", uniqueConstraints =
        @UniqueConstraint(name = "uk_vote_report_user", columnNames = {"report_id", "user_id"}))
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoteValue valeur;

    /** poids du vote : 1 normalement, 2 pour un contributeur de confiance */
    @Column(nullable = false)
    private Integer poids = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public VoteValue getValeur() { return valeur; }
    public void setValeur(VoteValue valeur) { this.valeur = valeur; }
    public Integer getPoids() { return poids; }
    public void setPoids(Integer poids) { this.poids = poids; }
}
