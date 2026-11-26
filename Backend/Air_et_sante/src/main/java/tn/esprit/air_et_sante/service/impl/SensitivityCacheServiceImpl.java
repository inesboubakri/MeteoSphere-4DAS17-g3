package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.SensitivityCache;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.ISensitivityCacheRepository;
import tn.esprit.air_et_sante.service.ISensitivityCacheService;

@Service
@RequiredArgsConstructor
public class SensitivityCacheServiceImpl implements ISensitivityCacheService {

    private final ISensitivityCacheRepository sensitivityCacheRepository;

    @Override
    public SensitivityCache create(SensitivityCache sensitivityCache) {
        if (sensitivityCache.getIdCache() != null) {
            throw new IllegalArgumentException("Un nouvel objet Sensibilité ne doit pas avoir d'identifiant");
        }
        verifierRegles(sensitivityCache);
        return sensitivityCacheRepository.save(sensitivityCache);
    }

    @Override
    public SensitivityCache findById(Long id) {
        return sensitivityCacheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensibilité", id));
    }

    @Override
    public List<SensitivityCache> findAll() {
        return sensitivityCacheRepository.findAll();
    }

    @Override
    public SensitivityCache update(Long id, SensitivityCache sensitivityCache) {
        verifierRegles(sensitivityCache);
        SensitivityCache existant = findById(id);
        existant.setUserId(sensitivityCache.getUserId());
        existant.setProfil(sensitivityCache.getProfil());
        existant.setDateMiseAJour(sensitivityCache.getDateMiseAJour());
        return sensitivityCacheRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!sensitivityCacheRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sensibilité", id);
        }
        sensitivityCacheRepository.deleteById(id);
    }

    private void verifierRegles(SensitivityCache sensitivityCache) {
        if (sensitivityCache.getUserId() == null) {
            throw new IllegalArgumentException("L'identifiant de l'utilisateur est obligatoire");
        }
    }
}
