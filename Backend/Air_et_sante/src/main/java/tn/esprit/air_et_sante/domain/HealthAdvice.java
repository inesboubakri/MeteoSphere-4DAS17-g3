package tn.esprit.air_et_sante.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.air_et_sante.domain.enums.IndiceQualiteAir;
import tn.esprit.air_et_sante.domain.enums.ProfilSante;

@Entity
@Table(name = "health_advice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthAdvice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdvice;

    @Enumerated(EnumType.STRING)
    private IndiceQualiteAir niveau;
    @Enumerated(EnumType.STRING)
    private ProfilSante profil;
    private String message;

    @ManyToOne
    private Pollutant pollutant;
}
