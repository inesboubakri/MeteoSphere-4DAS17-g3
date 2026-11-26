package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.AirZone;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.IAirZoneRepository;
import tn.esprit.air_et_sante.service.IAirZoneService;

@Service
@RequiredArgsConstructor
public class AirZoneServiceImpl implements IAirZoneService {

    private final IAirZoneRepository zoneRepository;

    @Override
    public AirZone create(AirZone zone) {
        if (zone.getIdZone() != null) {
            throw new IllegalArgumentException("Un nouvel objet Zone ne doit pas avoir d'identifiant");
        }
        verifierRegles(zone);
        return zoneRepository.save(zone);
    }

    @Override
    public AirZone findById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", id));
    }

    @Override
    public List<AirZone> findAll() {
        return zoneRepository.findAll();
    }

    @Override
    public AirZone update(Long id, AirZone zone) {
        verifierRegles(zone);
        AirZone existant = findById(id);
        existant.setNom(zone.getNom());
        existant.setLatitude(zone.getLatitude());
        existant.setLongitude(zone.getLongitude());
        existant.setType(zone.getType());
        return zoneRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!zoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Zone", id);
        }
        zoneRepository.deleteById(id);
    }

    private void verifierRegles(AirZone zone) {
        if (zone.getNom() == null || zone.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la zone est obligatoire");
        }
    }
}
