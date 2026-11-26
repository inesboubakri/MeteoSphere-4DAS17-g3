package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.HealthAdvice;

public interface IHealthAdviceService {
    HealthAdvice create(HealthAdvice healthAdvice);
    HealthAdvice findById(Long id);
    List<HealthAdvice> findAll();
    HealthAdvice update(Long id, HealthAdvice healthAdvice);
    void deleteById(Long id);
}
