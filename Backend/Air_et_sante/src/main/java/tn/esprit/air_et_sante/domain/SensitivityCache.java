package tn.esprit.air_et_sante.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.air_et_sante.domain.enums.ProfilSante;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "sensitivity_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensitivityCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCache;

    private Long userId;
    @Enumerated(EnumType.STRING)
    private ProfilSante profil;
    private LocalDateTime dateMiseAJour;

    @ManyToMany
    private List<Pollutant> pollutants;
}
