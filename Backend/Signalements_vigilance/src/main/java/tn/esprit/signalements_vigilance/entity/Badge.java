package tn.esprit.signalements_vigilance.entity;

import tn.esprit.signalements_vigilance.enums.BadgeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "badges", uniqueConstraints =
        @UniqueConstraint(name = "uk_badge_user_type", columnNames = {"user_id", "type"}))
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BadgeType type;

    @Column(name = "obtenu_le", nullable = false)
    private LocalDateTime obtenuLe;

    @PrePersist
    void onCreate() {
        if (obtenuLe == null) obtenuLe = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }
    public LocalDateTime getObtenuLe() { return obtenuLe; }
    public void setObtenuLe(LocalDateTime obtenuLe) { this.obtenuLe = obtenuLe; }
}
