package tn.esprit.prevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.prevision.domain.City;

public interface ICityRepository extends JpaRepository<City, Long> {
}
