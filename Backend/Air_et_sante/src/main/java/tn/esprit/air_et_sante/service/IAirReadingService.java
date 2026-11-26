package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.AirReading;

public interface IAirReadingService {
    AirReading create(AirReading airReading);
    AirReading findById(Long id);
    List<AirReading> findAll();
    AirReading update(Long id, AirReading airReading);
    void deleteById(Long id);
}
