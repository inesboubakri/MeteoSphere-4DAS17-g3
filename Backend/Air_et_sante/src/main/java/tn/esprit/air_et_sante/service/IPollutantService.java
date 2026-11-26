package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.Pollutant;

public interface IPollutantService {
    Pollutant create(Pollutant pollutant);
    Pollutant findById(Long id);
    List<Pollutant> findAll();
    Pollutant update(Long id, Pollutant pollutant);
    void deleteById(Long id);
}
