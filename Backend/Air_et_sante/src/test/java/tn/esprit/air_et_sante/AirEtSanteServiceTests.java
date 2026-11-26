package tn.esprit.air_et_sante;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.air_et_sante.domain.*;
import tn.esprit.air_et_sante.domain.enums.*;
import tn.esprit.air_et_sante.exception.ResourceNotFoundException;
import tn.esprit.air_et_sante.service.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AirEtSanteServiceTests {

    @Autowired
    private IAirZoneService airZoneService;

    @Autowired
    private IPollutantService pollutantService;

    @Autowired
    private IPollutionSourceService pollutionSourceService;

    @Autowired
    private IAirReadingService airReadingService;

    @Autowired
    private IHealthAdviceService healthAdviceService;

    @Autowired
    private ISensitivityCacheService sensitivityCacheService;

    // Partie 2 – Test guidé de IAirZoneService
    @Test
    void testAirZoneService() {
        AirZone zone = new AirZone();
        zone.setNom("Port de Sfax");
        zone.setLatitude(34.73);
        zone.setLongitude(10.76);
        zone.setType(TypeZone.COTIER);

        AirZone created = airZoneService.create(zone);
        System.out.println("create() -> id=" + created.getIdZone() + ", nom=" + created.getNom() + ", type=" + created.getType());
        assertNotNull(created.getIdZone());

        List<AirZone> zones = airZoneService.findAll();
        System.out.println("findAll() -> " + zones.size() + " zone(s)");
        zones.forEach(z -> System.out.println("   " + z.getIdZone() + " : " + z.getNom()));

        AirZone found = airZoneService.findById(created.getIdZone());
        System.out.println("findById(" + created.getIdZone() + ") -> " + found.getNom());
        assertEquals("Port de Sfax", found.getNom());

        // le service n'expose pas count() : le nombre est la taille de findAll()
        System.out.println("nombre de zones -> " + airZoneService.findAll().size());

        airZoneService.deleteById(created.getIdZone());
        Long id = created.getIdZone();
        assertThrows(ResourceNotFoundException.class, () -> airZoneService.findById(id));
        System.out.println("après deleteById -> la zone " + id + " n'existe plus");
    }

    // Partie 3 – Test guidé de IPollutantService
    @Test
    void testPollutantService() {
        Pollutant no2 = new Pollutant();
        no2.setNom("Dioxyde d'azote");
        no2.setSymbole("NO2");
        no2.setUnite("µg/m3");
        no2.setEffetsSante("Inflammation des voies respiratoires");
        no2.setSeuilAlerte(400.0);

        Pollutant o3 = new Pollutant();
        o3.setNom("Ozone");
        o3.setSymbole("O3");
        o3.setUnite("µg/m3");
        o3.setEffetsSante("Toux, essoufflement");
        o3.setSeuilAlerte(240.0);

        Pollutant savedNo2 = pollutantService.create(no2);
        Pollutant savedO3 = pollutantService.create(o3);
        assertNotNull(savedNo2.getIdPollutant());
        assertNotNull(savedO3.getIdPollutant());

        List<Pollutant> pollutants = pollutantService.findAll();
        System.out.println("findAll() -> " + pollutants.size() + " polluant(s)");
        pollutants.forEach(p -> System.out.println("   " + p.getIdPollutant() + " : " + p.getSymbole()));

        Pollutant found = pollutantService.findById(savedO3.getIdPollutant());
        System.out.println("findById(" + savedO3.getIdPollutant() + ") -> " + found.getSymbole());
        assertEquals("O3", found.getSymbole());

        System.out.println("nombre de polluants -> " + pollutantService.findAll().size());

        pollutantService.deleteById(savedNo2.getIdPollutant());
        Long id = savedNo2.getIdPollutant();
        assertThrows(ResourceNotFoundException.class, () -> pollutantService.findById(id));
        System.out.println("après deleteById -> le polluant " + id + " n'existe plus");

    }

    // Partie 4 – Test guidé de IPollutionSourceService
    @Test
    void testPollutionSourceService() {
        PollutionSource source = new PollutionSource();
        source.setNom("Route nationale RN1");
        source.setType(TypeSourcePollution.ROUTE);
        source.setLatitude(36.8);
        source.setLongitude(10.18);
        source.setTypeEmission("Gaz d'échappement");

        PollutionSource created = pollutionSourceService.create(source);
        System.out.println("create() -> id=" + created.getIdSource() + ", nom=" + created.getNom());
        assertNotNull(created.getIdSource());

        System.out.println("findAll() -> " + pollutionSourceService.findAll().size() + " source(s)");

        PollutionSource found = pollutionSourceService.findById(created.getIdSource());
        System.out.println("findById(" + created.getIdSource() + ") -> " + found.getNom());
        assertEquals(TypeSourcePollution.ROUTE, found.getType());

        System.out.println("nombre de sources -> " + pollutionSourceService.findAll().size());

        pollutionSourceService.deleteById(created.getIdSource());
        Long id = created.getIdSource();
        assertThrows(ResourceNotFoundException.class, () -> pollutionSourceService.findById(id));
        System.out.println("après deleteById -> la source " + id + " n'existe plus");
    }

    // Partie 5 – Exercice autonome : IAirReadingService (avec update)
    @Test
    void testAirReadingService() {
        AirReading reading = new AirReading();
        reading.setConcentration(35.0);
        reading.setDateMesure(LocalDateTime.now());
        reading.setSource(SourceReleve.API);
        reading.setIndice(IndiceQualiteAir.MOYEN);

        AirReading created = airReadingService.create(reading);
        System.out.println("create() -> id=" + created.getIdReading() + ", concentration=" + created.getConcentration());
        assertNotNull(created.getIdReading());

        AirReading modification = new AirReading();
        modification.setConcentration(90.0);
        modification.setDateMesure(created.getDateMesure());
        modification.setSource(SourceReleve.API);
        modification.setIndice(IndiceQualiteAir.MAUVAIS);
        AirReading updated = airReadingService.update(created.getIdReading(), modification);
        System.out.println("update() -> concentration=" + updated.getConcentration() + ", indice=" + updated.getIndice());
        assertEquals(IndiceQualiteAir.MAUVAIS, airReadingService.findById(created.getIdReading()).getIndice());

        airReadingService.deleteById(created.getIdReading());
        Long id = created.getIdReading();
        assertThrows(ResourceNotFoundException.class, () -> airReadingService.findById(id));
        System.out.println("après deleteById -> le relevé " + id + " n'existe plus");
    }

    // Partie 5 – Exercice autonome : IHealthAdviceService
    @Test
    void testHealthAdviceService() {
        HealthAdvice advice = new HealthAdvice();
        advice.setNiveau(IndiceQualiteAir.TRES_MAUVAIS);
        advice.setProfil(ProfilSante.SPORTIF);
        advice.setMessage("Reportez votre entraînement en extérieur");

        HealthAdvice created = healthAdviceService.create(advice);
        System.out.println("create() -> id=" + created.getIdAdvice() + ", message=" + created.getMessage());
        assertNotNull(created.getIdAdvice());

        System.out.println("findAll() -> " + healthAdviceService.findAll().size() + " conseil(s)");
        assertEquals(ProfilSante.SPORTIF, healthAdviceService.findById(created.getIdAdvice()).getProfil());

        healthAdviceService.deleteById(created.getIdAdvice());
        Long id = created.getIdAdvice();
        assertThrows(ResourceNotFoundException.class, () -> healthAdviceService.findById(id));
        System.out.println("après deleteById -> le conseil " + id + " n'existe plus");
    }

    // Partie 5 – Exercice autonome : ISensitivityCacheService
    @Test
    void testSensitivityCacheService() {
        SensitivityCache cache = new SensitivityCache();
        cache.setUserId(2L);
        cache.setProfil(ProfilSante.ENFANT);
        cache.setDateMiseAJour(LocalDateTime.now());

        SensitivityCache created = sensitivityCacheService.create(cache);
        System.out.println("create() -> id=" + created.getIdCache() + ", userId=" + created.getUserId());
        assertNotNull(created.getIdCache());

        SensitivityCache found = sensitivityCacheService.findById(created.getIdCache());
        System.out.println("findById(" + created.getIdCache() + ") -> profil=" + found.getProfil());
        assertEquals(ProfilSante.ENFANT, found.getProfil());

        sensitivityCacheService.deleteById(created.getIdCache());
        Long id = created.getIdCache();
        assertThrows(ResourceNotFoundException.class, () -> sensitivityCacheService.findById(id));
        System.out.println("après deleteById -> la sensibilité " + id + " n'existe plus");
    }
}
