# Atelier 5 — API REST Air & Santé et documentation Swagger

Le préfixe `/api` est défini une seule fois dans `application.properties` (`server.servlet.context-path=/api`).
Les contrôleurs n'écrivent donc que `/air-zones`, `/pollutants`, etc. L'application tourne sur le port 8086 :
- Swagger UI : http://localhost:8086/api/swagger-ui.html
- Description OpenAPI : http://localhost:8086/api/v3/api-docs

Les tests se font avec Swagger UI (Try it out > Execute).

## A. Endpoints

Les six contrôleurs suivent le même schéma. Le contrôleur ne produit aucune erreur lui-même : le service lève
`ResourceNotFoundException` (traduite en 404) ou `IllegalArgumentException` (traduite en 400) par le `GlobalExceptionHandler`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| AirZoneController | GET /api/air-zones | findAll() | 200 | — |
| AirZoneController | GET /api/air-zones/{id} | findById(id) | 200 | 404 |
| AirZoneController | POST /api/air-zones | create(airZone) | 201 | 400 (identifiant fourni, nom vide) |
| AirZoneController | PUT /api/air-zones/{id} | update(id, airZone) | 200 | 404, 400 (nom vide) |
| AirZoneController | DELETE /api/air-zones/{id} | deleteById(id) | 204 | 404 |
| PollutantController | GET /api/pollutants | findAll() | 200 | — |
| PollutantController | GET /api/pollutants/{id} | findById(id) | 200 | 404 |
| PollutantController | POST /api/pollutants | create(pollutant) | 201 | 400 (identifiant fourni, symbole vide, seuil négatif) |
| PollutantController | PUT /api/pollutants/{id} | update(id, pollutant) | 200 | 404, 400 |
| PollutantController | DELETE /api/pollutants/{id} | deleteById(id) | 204 | 404 |
| AirReadingController | GET /api/air-readings | findAll() | 200 | — |
| AirReadingController | GET /api/air-readings/{id} | findById(id) | 200 | 404 |
| AirReadingController | POST /api/air-readings | create(airReading) | 201 | 400 (identifiant fourni, concentration absente ou négative) |
| AirReadingController | PUT /api/air-readings/{id} | update(id, airReading) | 200 | 404, 400 |
| AirReadingController | DELETE /api/air-readings/{id} | deleteById(id) | 204 | 404 |
| PollutionSourceController | GET /api/pollution-sources | findAll() | 200 | — |
| PollutionSourceController | GET /api/pollution-sources/{id} | findById(id) | 200 | 404 |
| PollutionSourceController | POST /api/pollution-sources | create(pollutionSource) | 201 | 400 (identifiant fourni, nom vide) |
| PollutionSourceController | PUT /api/pollution-sources/{id} | update(id, pollutionSource) | 200 | 404, 400 |
| PollutionSourceController | DELETE /api/pollution-sources/{id} | deleteById(id) | 204 | 404 |
| HealthAdviceController | GET /api/health-advices | findAll() | 200 | — |
| HealthAdviceController | GET /api/health-advices/{id} | findById(id) | 200 | 404 |
| HealthAdviceController | POST /api/health-advices | create(healthAdvice) | 201 | 400 (identifiant fourni, message vide) |
| HealthAdviceController | PUT /api/health-advices/{id} | update(id, healthAdvice) | 200 | 404, 400 |
| HealthAdviceController | DELETE /api/health-advices/{id} | deleteById(id) | 204 | 404 |
| SensitivityCacheController | GET /api/sensitivities | findAll() | 200 | — |
| SensitivityCacheController | GET /api/sensitivities/{id} | findById(id) | 200 | 404 |
| SensitivityCacheController | POST /api/sensitivities | create(sensitivityCache) | 201 | 400 (identifiant fourni, userId absent) |
| SensitivityCacheController | PUT /api/sensitivities/{id} | update(id, sensitivityCache) | 200 | 404, 400 |
| SensitivityCacheController | DELETE /api/sensitivities/{id} | deleteById(id) | 204 | 404 |

## B. Annotations Jackson

Air & Santé n'a pas de composition (aucun enfant créé dans le JSON de son parent, aucune `cascade = ALL`).
Toutes les associations sont du cas « enfant créé seul, qui désigne son parent par un identifiant » :
`@JsonIgnore` sur la liste côté inverse, aucune annotation sur l'attribut propriétaire.

| Association | Annotation retenue | Justification |
|---|---|---|
| AirZone.airReadings (inverse de AirReading.airZone) | `@JsonIgnore` | Le relevé est créé seul et désigne sa zone : `{"airZone": {"idZone": 1}}`. |
| AirZone.pollutionSources (inverse de PollutionSource.airZone) | `@JsonIgnore` | La source est créée seule et désigne sa zone. |
| Pollutant.airReadings (inverse de AirReading.pollutant) | `@JsonIgnore` | Le relevé désigne son polluant : `{"pollutant": {"idPollutant": 1}}`. |
| Pollutant.healthAdvices (inverse de HealthAdvice.pollutant) | `@JsonIgnore` | Le conseil désigne son polluant. |
| Pollutant.pollutionSources (inverse du ManyToMany PollutionSource.pollutants) | `@JsonIgnore` | La source porte la liste de ses polluants ; le côté inverse n'est ni lu ni écrit. |
| Pollutant.sensitivityCaches (inverse du ManyToMany SensitivityCache.pollutants) | `@JsonIgnore` | Même raison. |

Sans ces annotations, Jackson boucle à l'infini (une zone contient ses relevés, chaque relevé contient sa zone...).

**Différence entre `@JsonBackReference` et `@JsonIgnore`.** `@JsonBackReference` s'utilise en paire avec
`@JsonManagedReference` (même nom) pour une composition : le champ est ignoré en sortie et Jackson reconstitue le lien
avec le parent à la lecture du JSON. `@JsonIgnore` exclut simplement le champ, dans les deux sens, sans rien
reconstituer. Sur l'attribut propriétaire (par exemple `AirReading.airZone`), `@JsonBackReference` serait une erreur :
le champ serait ignoré à la lecture et le POST créerait un relevé sans zone.

## C. Swagger

SpringDoc génère la description OpenAPI en analysant les contrôleurs au démarrage. Sans aucune annotation, les
endpoints sont déjà listés ; `OpenApiConfig` donne le titre et la description de l'API, et les annotations `@Tag`,
`@Operation`, `@ApiResponse` et `@Parameter` rendent la page lisible : six groupes (Zones, Polluants, Relevés,
Sources de pollution, Conseils santé, Sensibilités), un résumé par endpoint et les codes de réponse documentés.

Swagger UI sert aussi à essayer l'API : « Try it out » puis « Execute » envoie une vraie requête et affiche la commande
curl, le code et le corps de la réponse. Les données sont donc réellement enregistrées en base.

Exemple concret : le corps d'exemple que Swagger génère pour `POST /api/air-zones` contient `"idZone": 0`. Exécuté
tel quel, il renvoie **400** (« Un nouvel objet Zone ne doit pas avoir d'identifiant ») : il faut retirer l'identifiant
du corps pour obtenir **201**. Swagger construit son exemple à partir de la classe Java, sans connaître la règle
métier du service ; c'est une limite de l'exposition directe des entités, que les DTO corrigeront.

## D. Anomalies SonarQube

Analyse SonarQube for IDE : **13 anomalies dans 7 fichiers**, 0 Security Hotspot.

| Anomalie | Règle / explication | Correction |
|---|---|---|
| « Replace this persistent entity with a simple POJO or DTO object. » — 12 occurrences : méthodes `create` et `update` des 6 contrôleurs (paramètre `@RequestBody`). | **java:S4684** : le contrôleur reçoit directement une entité JPA. Risque de *mass assignment* : le client peut envoyer dans le JSON des champs qu'il ne devrait pas modifier (identifiant, objets liés), puisque l'entité est liée à la base. | **Non corrigée dans cet atelier** : l'énoncé précise que l'exposition directe des entités est volontaire et provisoire, et que les DTO arrivent au Chapitre 10. Risque atténué par les services : `create()` refuse tout identifiant fourni et `update()` ne recopie que les attributs modifiables. |
| « Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method. » — `GlobalExceptionHandler`, `LocalDateTime.now()`. | **java:S8688** : sans fuseau explicite, l'horodatage dépend implicitement du fuseau de la machine, ce qui rend le comportement moins prévisible (serveur, conteneur Docker…). | `LocalDateTime.now(ZoneId.systemDefault())` : le fuseau utilisé est maintenant explicite. |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?**
   Le `DispatcherServlet`, point d'entrée unique de Spring MVC. Il interroge le `HandlerMapping`, qui trouve le
   contrôleur et la méthode à partir du verbe HTTP et de l'URL (par exemple `GET /api/air-zones/5` correspond à
   `@GetMapping("/{id}")` de `AirZoneController`). Jackson convertit ensuite le JSON reçu en objet Java et l'objet
   retourné en JSON.
2. **Pourquoi `AirZoneController` dépend-il de `IAirZoneService` et non de `IAirZoneRepository` ni de
   `AirZoneServiceImpl` ?** Pour respecter les couches : le contrôleur délègue au service, qui porte les règles métier
   (400, 404). Dépendre de l'interface découple le contrôleur de l'implémentation, qu'on peut changer sans le modifier.
   Passer par le repository sauterait ces règles.
3. **Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?**
   201 (Created) indique qu'une ressource a été créée, et la réponse contient l'objet avec son identifiant. 204
   (No Content) indique que la suppression a réussi et qu'il n'y a rien à renvoyer. Une ressource absente est une erreur
   du client, donc 404 ; un 500 signifierait une panne du serveur. Le service lève `ResourceNotFoundException`, que le
   `GlobalExceptionHandler` traduit en 404 avec un corps JSON (statut, message, horodatage).
4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?**
   Dans AutoLoc, le paiement n'existe qu'à travers son contrat (composition, `cascade = ALL`) : il est créé, modifié et
   supprimé via le contrat. Un POST sur `/api/paiements` reçoit **405 Method Not Allowed**, car aucune méthode POST
   n'est mappée sur cette route. Air & Santé n'a pas de composition : les six contrôleurs exposent le CRUD complet.
5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il
   `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?** Couplage entre l'API et le schéma de
   base, champs internes visibles (identifiants, relations), boucles de sérialisation, et *mass assignment* (le client
   peut envoyer des champs qu'il ne devrait pas modifier). Les DTO corrigeront cela. `Contrat.paiements` est une
   composition : les paiements naissent dans le JSON du contrat, il faut la paire `@JsonManagedReference` /
   `@JsonBackReference` pour les garder visibles et reconstituer le lien. `Agence.vehicules` est une liste inverse :
   le véhicule est créé seul et désigne son agence, la liste n'a besoin d'être ni lue ni écrite, `@JsonIgnore` suffit.
   C'est le même cas que toutes les listes inverses d'Air & Santé.
