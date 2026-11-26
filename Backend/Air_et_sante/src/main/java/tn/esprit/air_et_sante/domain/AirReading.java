package tn.esprit.air_et_sante.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.air_et_sante.domain.enums.IndiceQualiteAir;
import tn.esprit.air_et_sante.domain.enums.SourceReleve;

import java.time.LocalDateTime;

@Entity
@Table(name = "air_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReading;

    private Double concentration;
    private LocalDateTime dateMesure;
    @Enumerated(EnumType.STRING)
    private SourceReleve source;
    @Enumerated(EnumType.STRING)
    private IndiceQualiteAir indice;

    @ManyToOne
    private AirZone airZone;

    @ManyToOne
    private Pollutant pollutant;
}
