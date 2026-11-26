package tn.esprit.prevision.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.prevision.domain.ForecastAccuracy;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.repository.IForecastAccuracyRepository;
import tn.esprit.prevision.service.IForecastAccuracyService;

@Service
@RequiredArgsConstructor
public class ForecastAccuracyServiceImpl implements IForecastAccuracyService {

    private final IForecastAccuracyRepository accuracyRepository;

    @Override
    public ForecastAccuracy create(ForecastAccuracy accuracy) {
        if (accuracy.getIdAccuracy() != null) {
            throw new IllegalArgumentException("Une nouvelle mesure de précision ne doit pas avoir d'identifiant");
        }
        verifierRegles(accuracy);
        return accuracyRepository.save(accuracy);
    }

    @Override
    public ForecastAccuracy findById(Long id) {
        return accuracyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Précision de prévision", id));
    }

    @Override
    public List<ForecastAccuracy> findAll() {
        return accuracyRepository.findAll();
    }

    @Override
    public ForecastAccuracy update(Long id, ForecastAccuracy accuracy) {
        verifierRegles(accuracy);
        ForecastAccuracy existant = findById(id);
        existant.setDateEvaluation(accuracy.getDateEvaluation());
        existant.setMae(accuracy.getMae());
        existant.setRmse(accuracy.getRmse());
        existant.setForecastModel(accuracy.getForecastModel());
        return accuracyRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!accuracyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Précision de prévision", id);
        }
        accuracyRepository.deleteById(id);
    }

    private void verifierRegles(ForecastAccuracy accuracy) {
        if (accuracy.getMae() == null && accuracy.getRmse() == null) {
            throw new IllegalArgumentException("Au moins une métrique (MAE ou RMSE) est obligatoire");
        }
    }
}
