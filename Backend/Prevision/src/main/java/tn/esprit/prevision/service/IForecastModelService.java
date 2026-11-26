package tn.esprit.prevision.service;

import java.util.List;
import tn.esprit.prevision.domain.ForecastModel;

public interface IForecastModelService {
    ForecastModel create(ForecastModel model);
    ForecastModel findById(Long id);
    List<ForecastModel> findAll();
    ForecastModel update(Long id, ForecastModel model);
    void deleteById(Long id);
}
