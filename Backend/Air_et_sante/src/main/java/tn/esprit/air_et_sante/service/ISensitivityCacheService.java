package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.SensitivityCache;

public interface ISensitivityCacheService {
    SensitivityCache create(SensitivityCache sensitivityCache);
    SensitivityCache findById(Long id);
    List<SensitivityCache> findAll();
    SensitivityCache update(Long id, SensitivityCache sensitivityCache);
    void deleteById(Long id);
}
