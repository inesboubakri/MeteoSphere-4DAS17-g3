package tn.esprit.prevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.prevision.domain.WeatherCondition;

public interface IWeatherConditionRepository extends JpaRepository<WeatherCondition, Long> {
}
