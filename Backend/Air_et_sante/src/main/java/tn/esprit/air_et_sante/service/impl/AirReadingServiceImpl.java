package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.AirReading;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.IAirReadingRepository;
import tn.esprit.air_et_sante.service.IAirReadingService;

@Service
@RequiredArgsConstructor
public class AirReadingServiceImpl implements IAirReadingService {

    private final IAirReadingRepository airReadingRepository;

    @Override
    public AirReading create(AirReading airReading) {
        if (airReading.getIdReading() != null) {
            throw new IllegalArgumentException("Un nouvel objet Relevé ne doit pas avoir d'identifiant");
        }
        verifierRegles(airReading);
        return airReadingRepository.save(airReading);
    }

    @Override
    public AirReading findById(Long id) {
        return airReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relevé", id));
    }

    @Override
    public List<AirReading> findAll() {
        return airReadingRepository.findAll();
    }

    @Override
    public AirReading update(Long id, AirReading airReading) {
        verifierRegles(airReading);
        AirReading existant = findById(id);
        existant.setConcentration(airReading.getConcentration());
        existant.setDateMesure(airReading.getDateMesure());
        existant.setSource(airReading.getSource());
        existant.setIndice(airReading.getIndice());
        return airReadingRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!airReadingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Relevé", id);
        }
        airReadingRepository.deleteById(id);
    }

    private void verifierRegles(AirReading airReading) {
        if (airReading.getConcentration() == null || airReading.getConcentration() < 0) {
            throw new IllegalArgumentException("La concentration doit être positive ou nulle");
        }
    }
}
