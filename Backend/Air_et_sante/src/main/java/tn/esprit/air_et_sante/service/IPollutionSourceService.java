package tn.esprit.air_et_sante.service;

import java.util.List;
import tn.esprit.air_et_sante.domain.PollutionSource;

public interface IPollutionSourceService {
    PollutionSource create(PollutionSource pollutionSource);
    PollutionSource findById(Long id);
    List<PollutionSource> findAll();
    PollutionSource update(Long id, PollutionSource pollutionSource);
    void deleteById(Long id);
}
