package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.PollutionSource;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.IPollutionSourceRepository;
import tn.esprit.air_et_sante.service.IPollutionSourceService;

@Service
@RequiredArgsConstructor
public class PollutionSourceServiceImpl implements IPollutionSourceService {

    private final IPollutionSourceRepository pollutionSourceRepository;

    @Override
    public PollutionSource create(PollutionSource pollutionSource) {
        if (pollutionSource.getIdSource() != null) {
            throw new IllegalArgumentException("Un nouvel objet Source de pollution ne doit pas avoir d'identifiant");
        }
        verifierRegles(pollutionSource);
        return pollutionSourceRepository.save(pollutionSource);
    }

    @Override
    public PollutionSource findById(Long id) {
        return pollutionSourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Source de pollution", id));
    }

    @Override
    public List<PollutionSource> findAll() {
        return pollutionSourceRepository.findAll();
    }

    @Override
    public PollutionSource update(Long id, PollutionSource pollutionSource) {
        verifierRegles(pollutionSource);
        PollutionSource existant = findById(id);
        existant.setNom(pollutionSource.getNom());
        existant.setType(pollutionSource.getType());
        existant.setLatitude(pollutionSource.getLatitude());
        existant.setLongitude(pollutionSource.getLongitude());
        existant.setTypeEmission(pollutionSource.getTypeEmission());
        return pollutionSourceRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!pollutionSourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Source de pollution", id);
        }
        pollutionSourceRepository.deleteById(id);
    }

    private void verifierRegles(PollutionSource pollutionSource) {
        if (pollutionSource.getNom() == null || pollutionSource.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la source de pollution est obligatoire");
        }
    }
}
