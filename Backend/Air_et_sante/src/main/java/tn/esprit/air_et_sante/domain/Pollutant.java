package tn.esprit.air_et_sante.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "pollutants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pollutant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPollutant;

    private String nom;
    private String symbole;
    private String unite;
    private String effetsSante;
    private Double seuilAlerte;

    @OneToMany(mappedBy = "pollutant")
    @JsonIgnore
    private List<AirReading> airReadings;

    @OneToMany(mappedBy = "pollutant")
    @JsonIgnore
    private List<HealthAdvice> healthAdvices;

    @ManyToMany(mappedBy = "pollutants")
    @JsonIgnore
    private List<PollutionSource> pollutionSources;

    @ManyToMany(mappedBy = "pollutants")
    @JsonIgnore
    private List<SensitivityCache> sensitivityCaches;
}
