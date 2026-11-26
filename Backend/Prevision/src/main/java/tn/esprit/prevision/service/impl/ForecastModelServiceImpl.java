package tn.esprit.prevision.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.prevision.domain.ForecastModel;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.repository.IForecastModelRepository;
import tn.esprit.prevision.service.IForecastModelService;

@Service
@RequiredArgsConstructor
public class ForecastModelServiceImpl implements IForecastModelService {

    private final IForecastModelRepository modelRepository;

    @Override
    public ForecastModel create(ForecastModel model) {
        if (model.getIdModel() != null) {
            throw new IllegalArgumentException("Un nouveau modèle ne doit pas avoir d'identifiant");
        }
        verifierRegles(model);
        return modelRepository.save(model);
    }

    @Override
    public ForecastModel findById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modèle de prévision", id));
    }

    @Override
    public List<ForecastModel> findAll() {
        return modelRepository.findAll();
    }

    @Override
    public ForecastModel update(Long id, ForecastModel model) {
        verifierRegles(model);
        ForecastModel existant = findById(id);
        existant.setNom(model.getNom());
        existant.setVersion(model.getVersion());
        existant.setFournisseur(model.getFournisseur());
        existant.setActif(model.getActif());
        return modelRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!modelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Modèle de prévision", id);
        }
        modelRepository.deleteById(id);
    }

    private void verifierRegles(ForecastModel model) {
        if (model.getNom() == null || model.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du modèle est obligatoire");
        }
    }
}
