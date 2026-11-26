package tn.esprit.prevision.service;

import java.util.List;
import tn.esprit.prevision.domain.WeatherCondition;

public interface IWeatherConditionService {
    WeatherCondition create(WeatherCondition condition);
    WeatherCondition findById(Long id);
    List<WeatherCondition> findAll();
    WeatherCondition update(Long id, WeatherCondition condition);
    void deleteById(Long id);
}
