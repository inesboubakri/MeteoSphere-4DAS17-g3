package tn.esprit.prevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.prevision.domain.Forecast;

public interface IForecastRepository extends JpaRepository<Forecast, Long> {
}
