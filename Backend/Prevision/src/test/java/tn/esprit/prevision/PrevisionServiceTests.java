package tn.esprit.prevision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.esprit.prevision.domain.*;
import tn.esprit.prevision.domain.enums.HorizonPrevision;
import tn.esprit.prevision.exception.ResourceNotFoundException;
import tn.esprit.prevision.service.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PrevisionServiceTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Autowired
    private ICityService cityService;

    @Autowired
    private IWeatherConditionService conditionService;

    @Autowired
    private IForecastModelService modelService;

    @Autowired
    private IForecastService forecastService;

    @Autowired
    private IForecastAccuracyService accuracyService;

    @Test
    void testCityCrud() {
        City city = new City();
        city.setNom("Tunis");
        city.setPays("Tunisie");
        city.setLatitude(36.8065);
        city.setLongitude(10.1815);

        City created = cityService.create(city);
        assertNotNull(created.getIdCity());
        assertEquals("Tunis", cityService.findById(created.getIdCity()).getNom());

        City modif = new City();
        modif.setNom("Sfax");
        modif.setPays("Tunisie");
        modif.setLatitude(34.74);
        modif.setLongitude(10.76);
        City updated = cityService.update(created.getIdCity(), modif);
        assertEquals("Sfax", updated.getNom());

        assertFalse(cityService.findAll().isEmpty());

        Long id = created.getIdCity();
        cityService.deleteById(id);
        assertThrows(ResourceNotFoundException.class, () -> cityService.findById(id));
    }

    @Test
    void testWeatherConditionCrud() {
        WeatherCondition cond = new WeatherCondition();
        cond.setCode("CLEAR");
        cond.setLibelle("Ensoleillé");
        cond.setDescription("Ciel dégagé");

        WeatherCondition created = conditionService.create(cond);
        assertNotNull(created.getIdCondition());

        WeatherCondition modif = new WeatherCondition();
        modif.setCode("RAIN");
        modif.setLibelle("Pluie");
        modif.setDescription("Averses modérées");
        assertEquals("Pluie", conditionService.update(created.getIdCondition(), modif).getLibelle());

        Long id = created.getIdCondition();
        conditionService.deleteById(id);
        assertThrows(ResourceNotFoundException.class, () -> conditionService.findById(id));
    }

    @Test
    void testForecastModelAndAccuracyCrud() {
        ForecastModel model = new ForecastModel();
        model.setNom("Open-Meteo");
        model.setVersion("v1");
        model.setFournisseur("Open-Meteo");
        model.setActif(true);

        ForecastModel savedModel = modelService.create(model);
        assertNotNull(savedModel.getIdModel());

        ForecastAccuracy acc = new ForecastAccuracy();
        acc.setDateEvaluation(LocalDateTime.now());
        acc.setMae(1.2);
        acc.setRmse(1.8);
        acc.setForecastModel(savedModel);

        ForecastAccuracy savedAcc = accuracyService.create(acc);
        assertNotNull(savedAcc.getIdAccuracy());

        accuracyService.deleteById(savedAcc.getIdAccuracy());
        modelService.deleteById(savedModel.getIdModel());
    }

    @Test
    void testForecastCrud() {
        Forecast forecast = new Forecast();
        forecast.setDatePrevision(LocalDateTime.now().plusHours(3));
        forecast.setHorizon(HorizonPrevision.HORAIRE);
        forecast.setTemperature(24.5);
        forecast.setHumidite(60.0);
        forecast.setVitesseVent(12.0);
        forecast.setProbabilitePluie(10.0);

        Forecast created = forecastService.create(forecast);
        assertNotNull(created.getIdForecast());

        Forecast modif = new Forecast();
        modif.setDatePrevision(created.getDatePrevision());
        modif.setHorizon(HorizonPrevision.SEPT_JOURS);
        modif.setTemperature(26.0);
        assertEquals(26.0, forecastService.update(created.getIdForecast(), modif).getTemperature());

        Long id = created.getIdForecast();
        forecastService.deleteById(id);
        assertThrows(ResourceNotFoundException.class, () -> forecastService.findById(id));
    }

    @Test
    void testSwaggerEndpointsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
