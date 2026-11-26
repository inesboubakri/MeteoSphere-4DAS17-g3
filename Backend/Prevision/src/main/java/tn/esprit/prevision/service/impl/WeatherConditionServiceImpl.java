package tn.esprit.prevision.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.prevision.domain.WeatherCondition;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.repository.IWeatherConditionRepository;
import tn.esprit.prevision.service.IWeatherConditionService;

@Service
@RequiredArgsConstructor
public class WeatherConditionServiceImpl implements IWeatherConditionService {

    private final IWeatherConditionRepository conditionRepository;

    @Override
    public WeatherCondition create(WeatherCondition condition) {
        if (condition.getIdCondition() != null) {
            throw new IllegalArgumentException("Une nouvelle condition météo ne doit pas avoir d'identifiant");
        }
        verifierRegles(condition);
        return conditionRepository.save(condition);
    }

    @Override
    public WeatherCondition findById(Long id) {
        return conditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condition météo", id));
    }

    @Override
    public List<WeatherCondition> findAll() {
        return conditionRepository.findAll();
    }

    @Override
    public WeatherCondition update(Long id, WeatherCondition condition) {
        verifierRegles(condition);
        WeatherCondition existant = findById(id);
        existant.setCode(condition.getCode());
        existant.setLibelle(condition.getLibelle());
        existant.setDescription(condition.getDescription());
        return conditionRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!conditionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Condition météo", id);
        }
        conditionRepository.deleteById(id);
    }

    private void verifierRegles(WeatherCondition condition) {
        if (condition.getLibelle() == null || condition.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé de la condition météo est obligatoire");
        }
    }
}
