package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.AirZone;

public interface IAirZoneService {
    AirZone create(AirZone zone);
    AirZone findById(Long id);
    List<AirZone> findAll();
    AirZone update(Long id, AirZone zone);
    void deleteById(Long id);
}
