package tn.esprit.air_et_sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.air_et_sante.domain.PollutionSource;

public interface IPollutionSourceRepository extends JpaRepository<PollutionSource, Long> {
}
