package tn.esprit.prevision.service;

import java.util.List;
import tn.esprit.prevision.domain.ForecastAccuracy;

public interface IForecastAccuracyService {
    ForecastAccuracy create(ForecastAccuracy accuracy);
    ForecastAccuracy findById(Long id);
    List<ForecastAccuracy> findAll();
    ForecastAccuracy update(Long id, ForecastAccuracy accuracy);
    void deleteById(Long id);
}
