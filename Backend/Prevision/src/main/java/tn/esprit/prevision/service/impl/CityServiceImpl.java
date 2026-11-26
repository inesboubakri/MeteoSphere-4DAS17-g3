package tn.esprit.prevision.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.prevision.domain.City;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.repository.ICityRepository;
import tn.esprit.prevision.service.ICityService;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements ICityService {

    private final ICityRepository cityRepository;

    @Override
    public City create(City city) {
        if (city.getIdCity() != null) {
            throw new IllegalArgumentException("Une nouvelle ville ne doit pas avoir d'identifiant");
        }
        verifierRegles(city);
        return cityRepository.save(city);
    }

    @Override
    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville", id));
    }

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @Override
    public City update(Long id, City city) {
        verifierRegles(city);
        City existant = findById(id);
        existant.setNom(city.getNom());
        existant.setPays(city.getPays());
        existant.setLatitude(city.getLatitude());
        existant.setLongitude(city.getLongitude());
        return cityRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ville", id);
        }
        cityRepository.deleteById(id);
    }

    private void verifierRegles(City city) {
        if (city.getNom() == null || city.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la ville est obligatoire");
        }
    }
}
