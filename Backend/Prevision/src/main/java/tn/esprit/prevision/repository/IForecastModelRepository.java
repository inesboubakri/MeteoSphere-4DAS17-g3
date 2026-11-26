package tn.esprit.prevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.prevision.domain.ForecastModel;

public interface IForecastModelRepository extends JpaRepository<ForecastModel, Long> {
}
