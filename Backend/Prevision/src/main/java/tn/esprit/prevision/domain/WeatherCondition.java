package tn.esprit.prevision.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "weather_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCondition;

    private String code;
    private String libelle;
    private String description;

    @OneToMany(mappedBy = "weatherCondition")
    @JsonIgnore
    private List<Forecast> forecasts;
}
