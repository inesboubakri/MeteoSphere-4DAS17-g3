package tn.esprit.air_et_sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.air_et_sante.domain.AirReading;

public interface IAirReadingRepository extends JpaRepository<AirReading, Long> {
}
