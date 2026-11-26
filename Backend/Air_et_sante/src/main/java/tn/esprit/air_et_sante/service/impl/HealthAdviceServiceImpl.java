package tn.esprit.air_et_sante.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.air_et_sante.domain.HealthAdvice;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.repository.IHealthAdviceRepository;
import tn.esprit.air_et_sante.service.IHealthAdviceService;

@Service
@RequiredArgsConstructor
public class HealthAdviceServiceImpl implements IHealthAdviceService {

    private final IHealthAdviceRepository healthAdviceRepository;

    @Override
    public HealthAdvice create(HealthAdvice healthAdvice) {
        if (healthAdvice.getIdAdvice() != null) {
            throw new IllegalArgumentException("Un nouvel objet Conseil santé ne doit pas avoir d'identifiant");
        }
        verifierRegles(healthAdvice);
        return healthAdviceRepository.save(healthAdvice);
    }

    @Override
    public HealthAdvice findById(Long id) {
        return healthAdviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conseil santé", id));
    }

    @Override
    public List<HealthAdvice> findAll() {
        return healthAdviceRepository.findAll();
    }

    @Override
    public HealthAdvice update(Long id, HealthAdvice healthAdvice) {
        verifierRegles(healthAdvice);
        HealthAdvice existant = findById(id);
        existant.setNiveau(healthAdvice.getNiveau());
        existant.setProfil(healthAdvice.getProfil());
        existant.setMessage(healthAdvice.getMessage());
        return healthAdviceRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!healthAdviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conseil santé", id);
        }
        healthAdviceRepository.deleteById(id);
    }

    private void verifierRegles(HealthAdvice healthAdvice) {
        if (healthAdvice.getMessage() == null || healthAdvice.getMessage().isBlank()) {
            throw new IllegalArgumentException("Le message du conseil santé est obligatoire");
        }
    }
}
