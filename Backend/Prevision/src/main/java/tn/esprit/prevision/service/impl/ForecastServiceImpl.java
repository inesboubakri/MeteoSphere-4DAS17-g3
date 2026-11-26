package tn.esprit.prevision.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.prevision.domain.Forecast;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.repository.IForecastRepository;
import tn.esprit.prevision.service.IForecastService;

@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements IForecastService {

    private final IForecastRepository forecastRepository;

    @Override
    public Forecast create(Forecast forecast) {
        if (forecast.getIdForecast() != null) {
            throw new IllegalArgumentException("Une nouvelle prévision ne doit pas avoir d'identifiant");
        }
        verifierRegles(forecast);
        return forecastRepository.save(forecast);
    }

    @Override
    public Forecast findById(Long id) {
        return forecastRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prévision", id));
    }

    @Override
    public List<Forecast> findAll() {
        return forecastRepository.findAll();
    }

    @Override
    public Forecast update(Long id, Forecast forecast) {
        verifierRegles(forecast);
        Forecast existant = findById(id);
        existant.setDatePrevision(forecast.getDatePrevision());
        existant.setHorizon(forecast.getHorizon());
        existant.setTemperature(forecast.getTemperature());
        existant.setHumidite(forecast.getHumidite());
        existant.setVitesseVent(forecast.getVitesseVent());
        existant.setProbabilitePluie(forecast.getProbabilitePluie());
        existant.setCity(forecast.getCity());
        existant.setWeatherCondition(forecast.getWeatherCondition());
        existant.setForecastModel(forecast.getForecastModel());
        return forecastRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!forecastRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prévision", id);
        }
        forecastRepository.deleteById(id);
    }

    private void verifierRegles(Forecast forecast) {
        if (forecast.getTemperature() == null) {
            throw new IllegalArgumentException("La température est obligatoire");
        }
    }
}
