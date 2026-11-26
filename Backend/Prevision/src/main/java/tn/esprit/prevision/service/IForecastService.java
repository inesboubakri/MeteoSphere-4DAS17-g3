package tn.esprit.prevision.service;

import java.util.List;
import tn.esprit.prevision.domain.Forecast;

public interface IForecastService {
    Forecast create(Forecast forecast);
    Forecast findById(Long id);
    List<Forecast> findAll();
    Forecast update(Long id, Forecast forecast);
    void deleteById(Long id);
}
