package tn.esprit.signalements_vigilance.dto;

import tn.esprit.signalements_vigilance.enums.NotificationChannel;
import tn.esprit.signalements_vigilance.enums.RiskType;

import java.time.LocalDateTime;

/** Notification envoyee (journal en memoire, voir NotificationService). */
public record NotificationEvent(Long id, LocalDateTime envoyeLe, Long userId, NotificationChannel canal,
                                String zone, RiskType typeRisque, String titre, String message) {}
