package tn.esprit.prevision.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.prevision.domain.enums.HorizonPrevision;

import java.time.LocalDateTime;

@Entity
@Table(name = "forecasts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idForecast;

    private LocalDateTime datePrevision;

    @Enumerated(EnumType.STRING)
    private HorizonPrevision horizon;

    private Double temperature;
    private Double humidite;
    private Double vitesseVent;
    private Double probabilitePluie;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private WeatherCondition weatherCondition;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ForecastModel forecastModel;
}
