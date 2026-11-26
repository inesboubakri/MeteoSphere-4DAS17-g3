package tn.esprit.prevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.prevision.domain.ForecastAccuracy;

public interface IForecastAccuracyRepository extends JpaRepository<ForecastAccuracy, Long> {
}
