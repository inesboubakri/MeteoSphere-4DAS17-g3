package tn.esprit.air_et_sante.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.air_et_sante.domain.enums.TypeSourcePollution;

import java.util.*;

@Entity
@Table(name = "pollution_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollutionSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSource;

    private String nom;
    @Enumerated(EnumType.STRING)
    private TypeSourcePollution type;
    private Double latitude;
    private Double longitude;
    private String typeEmission;

    @ManyToOne
    private AirZone airZone;

    @ManyToMany
    private List<Pollutant> pollutants;
}
