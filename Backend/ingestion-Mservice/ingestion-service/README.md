# Microservice Ingestion — MeteoSphere

Projet Spring Boot indépendant du projet Eureka Server. Ouvrir ce dossier seul dans IntelliJ IDEA.

## Prérequis
- JDK 17
- Maven 3.9+ (ou Maven intégré à IntelliJ)
- MongoDB démarré sur `localhost:27017`
- Eureka Server déjà démarré sur `http://localhost:8761`

## Lancement
1. Démarrer MongoDB.
2. Démarrer ton projet Eureka indépendant (port 8761).
3. Ouvrir ce dossier `ingestion-service` dans IntelliJ.
4. Recharger le projet Maven puis lancer `tn.esprit.ingestion.IngestionApplication`.

Le microservice démarre sur le port `8081` et s'enregistre auprès d'Eureka sous le nom `INGESTION`.

## Organisation fonctionnelle des API

### Ressources administrables (CRUD complet)
- `Provider` : `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`
- `MonitoredZone` : `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`

### Ressources de suivi (lecture seule via l'API REST)
Ces ressources sont normalement produites ou mises à jour par le processus d'ingestion. L'API publique ne propose donc que la consultation :
- `RawObservation` : `GET /api/raw-observations` et `GET /api/raw-observations/{id}`
- `IngestionJob` : `GET /api/ingestion-jobs` et `GET /api/ingestion-jobs/{id}`
- `IngestionError` : `GET /api/ingestion-errors` et `GET /api/ingestion-errors/{id}`

Les opérations POST, PUT et DELETE ne sont volontairement pas exposées pour ces trois ressources. Cela évite la saisie ou la suppression manuelle d'observations, de traitements et d'erreurs qui doivent refléter l'activité réelle du processus d'ingestion.

**Note fonctionnelle :** dans cette version, les endpoints CRUD ne réalisent pas encore la collecte automatique auprès des fournisseurs. L'automatisation qui crée les jobs, observations et erreurs devra être ajoutée dans une évolution métier ultérieure.

## Actuator
- `GET http://localhost:8081/actuator/health`
- `GET http://localhost:8081/actuator/info`
- `GET http://localhost:8081/actuator/metrics`
- `GET http://localhost:8081/actuator/loggers`

La santé MongoDB est exposée par le HealthIndicator personnalisé.

## Configuration
Modifier `src/main/resources/application.properties` si MongoDB ou Eureka utilisent d'autres adresses/ports.
