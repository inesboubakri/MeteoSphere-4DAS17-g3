package tn.esprit.air_et_sante;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.air_et_sante.domain.*;
import tn.esprit.air_et_sante.domain.enums.*;
import tn.esprit.air_et_sante.repository.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AirEtSanteApplicationTests {

    @Autowired
    private IAirZoneRepository airZoneRepository;

    @Autowired
    private IPollutantRepository pollutantRepository;

    @Autowired
    private IPollutionSourceRepository pollutionSourceRepository;

    @Autowired
    private IAirReadingRepository airReadingRepository;

    @Autowired
    private IHealthAdviceRepository healthAdviceRepository;

    @Autowired
    private ISensitivityCacheRepository sensitivityCacheRepository;

    @Test
    void contextLoads() {
    }

    // Partie 2 – Test guidé de IAirZoneRepository
    @Test
    void testAirZoneRepository() {
        AirZone zone = new AirZone();
        zone.setNom("Zone industrielle de Gabès");
        zone.setLatitude(33.9);
        zone.setLongitude(10.1);
        zone.setType(TypeZone.INDUSTRIEL);

        AirZone saved = airZoneRepository.save(zone);
        System.out.println("save() -> id=" + saved.getIdZone() + ", nom=" + saved.getNom() + ", type=" + saved.getType());
        assertNotNull(saved.getIdZone());

        List<AirZone> zones = airZoneRepository.findAll();
        System.out.println("findAll() -> " + zones.size() + " zone(s)");
        zones.forEach(z -> System.out.println("   " + z.getIdZone() + " : " + z.getNom()));
        assertFalse(zones.isEmpty());

        Optional<AirZone> found = airZoneRepository.findById(saved.getIdZone());
        System.out.println("findById(" + saved.getIdZone() + ") -> " + found.map(AirZone::getNom).orElse("introuvable"));
        assertTrue(found.isPresent());

        boolean exists = airZoneRepository.existsById(saved.getIdZone());
        System.out.println("existsById(" + saved.getIdZone() + ") -> " + exists);
        assertTrue(exists);

        System.out.println("count() -> " + airZoneRepository.count());

        airZoneRepository.deleteById(saved.getIdZone());
        boolean existsAfterDelete = airZoneRepository.existsById(saved.getIdZone());
        System.out.println("après deleteById -> existsById = " + existsAfterDelete);
        assertFalse(existsAfterDelete);
    }

    // Partie 3 – Test guidé de IPollutantRepository
    @Test
    void testPollutantRepository() {
        Pollutant pm25 = new Pollutant();
        pm25.setNom("Particules fines");
        pm25.setSymbole("PM2.5");
        pm25.setUnite("µg/m3");
        pm25.setEffetsSante("Pénètrent dans les poumons, aggravent l'asthme");
        pm25.setSeuilAlerte(50.0);

        Pollutant so2 = new Pollutant();
        so2.setNom("Dioxyde de soufre");
        so2.setSymbole("SO2");
        so2.setUnite("µg/m3");
        so2.setEffetsSante("Irritation des voies respiratoires");
        so2.setSeuilAlerte(500.0);

        Pollutant savedPm25 = pollutantRepository.save(pm25);
        Pollutant savedSo2 = pollutantRepository.save(so2);
        assertNotNull(savedPm25.getIdPollutant());
        assertNotNull(savedSo2.getIdPollutant());

        List<Pollutant> pollutants = pollutantRepository.findAll();
        System.out.println("findAll() -> " + pollutants.size() + " polluant(s)");
        pollutants.forEach(p -> System.out.println("   " + p.getIdPollutant() + " : " + p.getSymbole() + " (" + p.getNom() + ")"));

        Optional<Pollutant> found = pollutantRepository.findById(savedSo2.getIdPollutant());
        System.out.println("findById(" + savedSo2.getIdPollutant() + ") -> " + found.map(Pollutant::getSymbole).orElse("introuvable"));
        assertEquals("SO2", found.map(Pollutant::getSymbole).orElse(null));

        boolean existing = pollutantRepository.existsById(savedPm25.getIdPollutant());
        boolean missing = pollutantRepository.existsById(999999L);
        System.out.println("existsById(" + savedPm25.getIdPollutant() + ") -> " + existing);
        System.out.println("existsById(999999) -> " + missing);
        assertTrue(existing);
        assertFalse(missing);

        long count = pollutantRepository.count();
        System.out.println("count() -> " + count);
        assertTrue(count >= 2);
    }

    // Partie 4 – Test guidé de IPollutionSourceRepository
    @Test
    void testPollutionSourceRepository() {
        PollutionSource source = new PollutionSource();
        source.setNom("Groupe chimique de Gabès");
        source.setType(TypeSourcePollution.USINE);
        source.setLatitude(33.91);
        source.setLongitude(10.12);
        source.setTypeEmission("Fumées");

        PollutionSource saved = pollutionSourceRepository.save(source);
        System.out.println("save() -> id=" + saved.getIdSource() + ", typeEmission=" + saved.getTypeEmission());
        assertNotNull(saved.getIdSource());

        List<PollutionSource> sources = pollutionSourceRepository.findAll();
        System.out.println("findAll() -> " + sources.size() + " source(s)");
        System.out.println("count() -> " + pollutionSourceRepository.count());

        saved.setTypeEmission("Fumées et poussières");
        PollutionSource updated = pollutionSourceRepository.save(saved);
        System.out.println("save() après modification -> typeEmission=" + updated.getTypeEmission());
        assertEquals("Fumées et poussières", updated.getTypeEmission());

        pollutionSourceRepository.deleteById(saved.getIdSource());
        System.out.println("après deleteById -> existsById = " + pollutionSourceRepository.existsById(saved.getIdSource()));
        assertFalse(pollutionSourceRepository.existsById(saved.getIdSource()));
    }

    // Partie 5 – Exercice autonome : IAirReadingRepository
    @Test
    void testAirReadingRepository() {
        AirReading reading = new AirReading();
        reading.setConcentration(180.0);
        reading.setDateMesure(LocalDateTime.now());
        reading.setSource(SourceReleve.CAPTEUR);
        reading.setIndice(IndiceQualiteAir.MAUVAIS);

        AirReading saved = airReadingRepository.save(reading);
        System.out.println("save() -> id=" + saved.getIdReading() + ", concentration=" + saved.getConcentration() + ", indice=" + saved.getIndice());
        assertNotNull(saved.getIdReading());

        Optional<AirReading> found = airReadingRepository.findById(saved.getIdReading());
        System.out.println("findById(" + saved.getIdReading() + ") -> " + found.map(AirReading::getIndice).orElse(null));
        assertTrue(found.isPresent());

        assertTrue(airReadingRepository.existsById(saved.getIdReading()));
        System.out.println("existsById(" + saved.getIdReading() + ") -> true");

        airReadingRepository.deleteById(saved.getIdReading());
        System.out.println("après deleteById -> existsById = " + airReadingRepository.existsById(saved.getIdReading()));
        assertFalse(airReadingRepository.existsById(saved.getIdReading()));
    }

    // Partie 5 – Exercice autonome : IHealthAdviceRepository
    @Test
    void testHealthAdviceRepository() {
        HealthAdvice advice = new HealthAdvice();
        advice.setNiveau(IndiceQualiteAir.MAUVAIS);
        advice.setProfil(ProfilSante.ASTHMATIQUE);
        advice.setMessage("Évitez de sortir, gardez votre inhalateur");

        HealthAdvice saved = healthAdviceRepository.save(advice);
        System.out.println("save() -> id=" + saved.getIdAdvice() + ", profil=" + saved.getProfil() + ", message=" + saved.getMessage());
        assertNotNull(saved.getIdAdvice());

        List<HealthAdvice> advices = healthAdviceRepository.findAll();
        System.out.println("findAll() -> " + advices.size() + " conseil(s)");
        assertFalse(advices.isEmpty());

        assertTrue(healthAdviceRepository.existsById(saved.getIdAdvice()));
        System.out.println("existsById(" + saved.getIdAdvice() + ") -> true");

        healthAdviceRepository.deleteById(saved.getIdAdvice());
        System.out.println("après deleteById -> existsById = " + healthAdviceRepository.existsById(saved.getIdAdvice()));
        assertFalse(healthAdviceRepository.existsById(saved.getIdAdvice()));
    }

    // Partie 5 – Exercice autonome : ISensitivityCacheRepository
    @Test
    void testSensitivityCacheRepository() {
        SensitivityCache cache = new SensitivityCache();
        cache.setUserId(1L);
        cache.setProfil(ProfilSante.ASTHMATIQUE);
        cache.setDateMiseAJour(LocalDateTime.now());

        SensitivityCache saved = sensitivityCacheRepository.save(cache);
        System.out.println("save() -> id=" + saved.getIdCache() + ", userId=" + saved.getUserId() + ", profil=" + saved.getProfil());
        assertNotNull(saved.getIdCache());

        Optional<SensitivityCache> found = sensitivityCacheRepository.findById(saved.getIdCache());
        System.out.println("findById(" + saved.getIdCache() + ") -> userId=" + found.map(SensitivityCache::getUserId).orElse(null));
        assertTrue(found.isPresent());

        assertTrue(sensitivityCacheRepository.existsById(saved.getIdCache()));
        System.out.println("existsById(" + saved.getIdCache() + ") -> true");

        sensitivityCacheRepository.deleteById(saved.getIdCache());
        System.out.println("après deleteById -> existsById = " + sensitivityCacheRepository.existsById(saved.getIdCache()));
        assertFalse(sensitivityCacheRepository.existsById(saved.getIdCache()));
    }
}
