# Atelier 3 — Couche Repository (Air & Santé)

Toutes les interfaces sont dans le package `tn.esprit.air_et_sante.repository`,
préfixées par `I`, et étendent `JpaRepository<Entité, Long>`.
Au démarrage, les logs affichent : `Found 6 JPA repository interfaces`.

## Choix des interfaces

| Interface | Étend | Justification |
|---|---|---|
| IAirZoneRepository | JpaRepository<AirZone, Long> | CRUD complet, findAll renvoie une List, tri et pagination pour lister les zones, saveAndFlush disponible. |
| IPollutantRepository | JpaRepository<Pollutant, Long> | CRUD complet sur le catalogue des polluants, findAll renvoie une List. |
| IAirReadingRepository | JpaRepository<AirReading, Long> | Table la plus volumineuse : la pagination (findAll(Pageable)) et le tri par date ou concentration sont utiles. |
| IPollutionSourceRepository | JpaRepository<PollutionSource, Long> | CRUD complet sur les sources de pollution, findAll renvoie une List. |
| IHealthAdviceRepository | JpaRepository<HealthAdvice, Long> | CRUD complet sur les conseils santé, findAll renvoie une List. |
| ISensitivityCacheRepository | JpaRepository<SensitivityCache, Long> | CRUD complet sur la copie des sensibilités ; saveAndFlush pour une mise à jour immédiate à la réception de user.sensitivity.updated. |

Remarque : le premier repository (IAirZoneRepository) a d'abord été créé avec
`CrudRepository` (étape 1), puis passé en `JpaRepository` (étape 2).

## Anomalies SonarQube for IDE

Analyse lancée sur tout le projet (clic droit > Analyze with SonarQube for IDE) :
**2 anomalies détectées dans 2 fichiers**, 0 Security Hotspot.

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
|---|---|---|
| `AirReading.java` (12:7) : Remove this unused import 'java.util'. | **java:S1128** – Unnecessary imports should be removed. Un import inutilisé alourdit le code et laisse croire à une dépendance qui n'existe pas (Clean Code). `AirReading` n'a que des relations `@ManyToOne`, donc aucune collection `java.util`. | Suppression de `import java.util.*;` dans `AirReading`. |
| `HealthAdvice.java` (11:7) : Remove this unused import 'java.util'. | **java:S1128** – même règle : `HealthAdvice` n'a qu'une relation `@ManyToOne` vers `Pollutant`, aucune `List`. | Suppression de `import java.util.*;` dans `HealthAdvice`. |

Remarque : l'import `java.util.*` avait été ajouté dans toutes les entités à l'Atelier 2.
Il reste utile dans `AirZone`, `Pollutant`, `PollutionSource` et `SensitivityCache`, qui utilisent `List`.
Après correction et nouvelle analyse : 0 anomalie.
