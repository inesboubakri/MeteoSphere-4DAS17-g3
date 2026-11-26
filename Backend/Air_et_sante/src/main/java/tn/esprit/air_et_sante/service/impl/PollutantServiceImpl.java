package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.Pollutant;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.IPollutantRepository;
import tn.esprit.air_et_sante.service.IPollutantService;

@Service
@RequiredArgsConstructor
public class PollutantServiceImpl implements IPollutantService {

    private final IPollutantRepository pollutantRepository;

    @Override
    public Pollutant create(Pollutant pollutant) {
        if (pollutant.getIdPollutant() != null) {
            throw new IllegalArgumentException("Un nouvel objet Polluant ne doit pas avoir d'identifiant");
        }
        verifierRegles(pollutant);
        return pollutantRepository.save(pollutant);
    }

    @Override
    public Pollutant findById(Long id) {
        return pollutantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Polluant", id));
    }

    @Override
    public List<Pollutant> findAll() {
        return pollutantRepository.findAll();
    }

    @Override
    public Pollutant update(Long id, Pollutant pollutant) {
        verifierRegles(pollutant);
        Pollutant existant = findById(id);
        existant.setNom(pollutant.getNom());
        existant.setSymbole(pollutant.getSymbole());
        existant.setUnite(pollutant.getUnite());
        existant.setEffetsSante(pollutant.getEffetsSante());
        existant.setSeuilAlerte(pollutant.getSeuilAlerte());
        return pollutantRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!pollutantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Polluant", id);
        }
        pollutantRepository.deleteById(id);
    }

    private void verifierRegles(Pollutant pollutant) {
        if (pollutant.getSymbole() == null || pollutant.getSymbole().isBlank()) {
            throw new IllegalArgumentException("Le symbole du polluant est obligatoire");
        }
        if (pollutant.getSeuilAlerte() != null && pollutant.getSeuilAlerte() < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas être négatif");
        }
    }
}
