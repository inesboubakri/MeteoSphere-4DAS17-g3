package tn.esprit.prevision.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "forecast_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idModel;

    private String nom;
    private String version;
    private String fournisseur;
    private Boolean actif;

    @OneToMany(mappedBy = "forecastModel")
    @JsonIgnore
    private List<Forecast> forecasts;

    @OneToMany(mappedBy = "forecastModel")
    @JsonIgnore
    private List<ForecastAccuracy> accuracies;
}
