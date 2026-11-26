package tn.esprit.air_et_sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.air_et_sante.domain.HealthAdvice;

public interface IHealthAdviceRepository extends JpaRepository<HealthAdvice, Long> {
}
