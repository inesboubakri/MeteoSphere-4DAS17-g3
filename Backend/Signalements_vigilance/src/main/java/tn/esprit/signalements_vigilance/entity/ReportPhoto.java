package tn.esprit.signalements_vigilance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_photos")
public class ReportPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 255)
    private String legende;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getLegende() { return legende; }
    public void setLegende(String legende) { this.legende = legende; }
}
