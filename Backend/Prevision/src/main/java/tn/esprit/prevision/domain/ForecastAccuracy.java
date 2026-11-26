package tn.esprit.prevision.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "forecast_accuracy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastAccuracy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccuracy;

    private LocalDateTime dateEvaluation;
    private Double mae;
    private Double rmse;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ForecastModel forecastModel;
}
