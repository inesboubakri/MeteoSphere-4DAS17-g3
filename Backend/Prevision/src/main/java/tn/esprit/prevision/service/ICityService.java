package tn.esprit.prevision.service;

import java.util.List;
import tn.esprit.prevision.domain.City;

public interface ICityService {
    City create(City city);
    City findById(Long id);
    List<City> findAll();
    City update(Long id, City city);
    void deleteById(Long id);
}
