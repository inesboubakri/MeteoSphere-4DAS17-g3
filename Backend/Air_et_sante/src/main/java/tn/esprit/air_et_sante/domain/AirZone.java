package tn.esprit.air_et_sante.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.air_et_sante.domain.enums.TypeZone;

import java.util.*;

@Entity
@Table(name = "air_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZone;

    private String nom;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private TypeZone type;

    @OneToMany(mappedBy = "airZone")
    @JsonIgnore
    private List<AirReading> airReadings;

    @OneToMany(mappedBy = "airZone")
    @JsonIgnore
    private List<PollutionSource> pollutionSources;
}
