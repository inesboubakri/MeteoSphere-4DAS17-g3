package tn.esprit.air_et_sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.air_et_sante.domain.SensitivityCache;

public interface ISensitivityCacheRepository extends JpaRepository<SensitivityCache, Long> {
}
