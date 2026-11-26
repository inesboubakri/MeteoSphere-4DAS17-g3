# Signalements & Vigilance (notifications en fonctionnalité)

Microservice n°5 de MeteoSphere : **Spring Boot + MySQL**, enregistré dans **Eureka**, documenté avec **Swagger**, testable avec **Postman**.

| | |
|---|---|
| Nom Eureka | `SIGNALEMENTS-VIGILANCE` (`spring.application.name=signalements-vigilance`) |
| Port | `8085` |
| Base | MySQL `meteosphere_signalements` (tests : H2) |
| Swagger UI | http://localhost:8085/swagger-ui.html |
| Actuator | `/actuator/health`, `/actuator/info`, `/actuator/metrics` |

## Lancer (dans cet ordre)

```bash
# 1. Eureka Server (dashboard : http://localhost:8761)
cd Backend/eureka && ./mvnw spring-boot:run

# 2. MySQL (Docker) – ou votre MySQL local : adapter DB_URL / DB_USER / DB_PASSWORD
cd Backend/Signalements_vigilance && docker compose up -d     # root / root

# 3. Le service (l'utilisateur root/root est lu depuis les variables d'environnement)
DB_PASSWORD=root ./mvnw spring-boot:run
```

Avec un MySQL local sans mot de passe root, `./mvnw spring-boot:run` suffit. La base est créée automatiquement
(`createDatabaseIfNotExist=true`, `ddl-auto=update`) et remplie par des données de démonstration au premier démarrage
(`app.seed.enabled=true`).

**Vérifier Eureka** : recharger http://localhost:8761 → `SIGNALEMENTS-VIGILANCE` apparaît (UP) en moins de 30 s.
Le statut vient d'Actuator (`eureka.client.healthcheck.enabled=true`) : si MySQL tombe, l'instance passe DOWN.

## Tester

* **Swagger** : http://localhost:8085/swagger-ui.html (endpoints regroupés par tag, essai direct avec « Try it out »).
* **Postman** : importer `postman/MeteoSphere-Signalements-Vigilance.postman_collection.json`, puis *Run collection*
  (55 requêtes avec scripts de test, à exécuter dans l'ordre : elles enchaînent les identifiants).
* **Tests automatiques** : `./mvnw test` (H2 en mémoire, sans Eureka ni MySQL).

## Les 8 tables et leur CRUD

| Table | Endpoints |
|---|---|
| `reports` | `POST/GET /reports`, `GET/PUT/DELETE /reports/{id}`, `PATCH /reports/{id}/status` |
| `report_photos` | `POST/GET /reports/{id}/photos`, `GET/PUT/DELETE /photos/{id}` |
| `comments` | `POST/GET /reports/{id}/comments`, `GET/PUT/DELETE /comments/{id}` |
| `votes` | `POST/GET /reports/{id}/votes`, `GET/PUT/DELETE /votes/{id}` |
| `badges` | `POST/GET /badges`, `GET/PUT/DELETE /badges/{id}` |
| `vigilance_zones` | `POST/GET /vigilance/zones`, `GET/PUT/DELETE /vigilance/zones/{id}`, `POST /vigilance/zones/recompute`, `GET /vigilance/map` (GeoJSON) |
| `official_bulletins` | `POST/GET /bulletins`, `GET/PUT/DELETE /bulletins/{id}` |
| `notification_subscriptions` | `POST/GET /subscriptions`, `GET/PUT/DELETE /subscriptions/{id}` |

Filtres et tris : `GET /reports?zone=&type=&statut=&userId=&from=&to=&sort=recent|votes|graves|fiabilite&page=&size=`,
`/vigilance/zones?zone=&type=&niveau=&sort=indice|recent|zone`, `/bulletins?activeOnly=true`, etc.
Statistiques : `GET /stats/reports` (par statut, type, zone, taux de validation, contributeurs les plus fiables).

## Fonctionnalités avancées (logique métier)

1. **Détection de doublons** : même zone, même type, rayon de 1 km, 24 h → `duplicateOf` renseigné, +5 de fiabilité à l'original.
2. **Score de fiabilité** (0-100) : 50 (60 si l'auteur est « contributeur fiable »), +5 par doublon, ±10 par vote (×2 pour un contributeur fiable). Pas de vote sur son propre signalement.
3. **Vigilance par zone** : indice = Σ gravité × 4 × fiabilité des signalements *validés* des dernières 24 h (doublons exclus), plancher imposé par un bulletin officiel actif. Vert 0-39, jaune 40-59, orange 60-79, rouge 80-100. Recalcul automatique à chaque validation, vote, bulletin ou suppression.
4. **Notifications** (fonctionnalité du service, pas un service à part) : quand le niveau d'une zone change, ou qu'un signalement grave (≥ 4) est validé, les abonnés de la zone (type de risque + canal APPLICATION / WEBSOCKET / EMAIL) sont notifiés. Consulter `GET /notifications?userId=`.
5. **Badges** attribués à la validation : 1 → `PREMIER_SIGNALEMENT`, 5 → `CONTRIBUTEUR_FIABLE`, 10 → `SENTINELLE`.

## Limites actuelles (étapes suivantes du cours)

* **Notifications** : l'envoi est simulé (log) et le journal est en mémoire. WebSocket/e-mail réels et heures de silence (stockées dans Utilisateurs & Santé) viendront plus tard.
* **JWT / Keycloak (séance 8)** : `userId` est passé dans le corps des requêtes ; les rôles (visiteur, citoyen, météorologue, admin) ne sont pas encore contrôlés.
* **API Gateway, OpenFeign (séance 6), RabbitMQ (séance 7)** : événements `measurement.received`, `forecast.updated`, `air.quality.changed` (lus) et `report.created`, `vigilance.level.changed` (publiés) non branchés. `vigilance.level.changed` correspond au point où `NotificationService.onLevelChanged` est déjà appelé.
* Le README racine mentionne encore H2 pour ce service (version 1 du concept) ; la version 2 impose MySQL, appliquée ici.
