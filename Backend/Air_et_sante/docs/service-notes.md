# Atelier 4 — IoC, injection de dépendances et couche Service (Air & Santé)

Organisation :
- `tn.esprit.air_et_sante.exception` : `ResourceNotFoundException`
- `tn.esprit.air_et_sante.service` : interfaces `I…Service`
- `tn.esprit.air_et_sante.service.impl` : implémentations `…ServiceImpl` (`@Service`, `@RequiredArgsConstructor`, champ `final` typé par l'interface `I…Repository`)

## A. Modes d'injection comparés (étape 2)

| Critère | Injection par constructeur | Injection par attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible ? | Oui | Non |
| Dépendance visible dans la signature de la classe ? | Oui, dans le constructeur | Non, cachée dans un champ privé |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : impossible de créer le service sans lui donner son repository | Non : le champ reste `null` hors du conteneur |
| Signalé par SonarQube for IDE ? | Non | Oui (règle à relever dans le panneau, S6813 annoncée par l'atelier) |

## B. Services

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| AirZoneServiceImpl | IAirZoneRepository | Constructeur (`@RequiredArgsConstructor`, champ `final`) | Dépendance obligatoire et immuable ; typée par l'interface, Spring fournit le proxy Spring Data. |
| PollutantServiceImpl | IPollutantRepository | Constructeur | Idem ; le service reste sans état (singleton partagé). |
| AirReadingServiceImpl | IAirReadingRepository | Constructeur | Idem ; aucun `new` de repository. |
| PollutionSourceServiceImpl | IPollutionSourceRepository | Constructeur | Idem. |
| HealthAdviceServiceImpl | IHealthAdviceRepository | Constructeur | Idem. |
| SensitivityCacheServiceImpl | ISensitivityCacheRepository | Constructeur | Idem. |

## C. Messages d'erreur (étape 3)

**Message 1** — `NoSuchBeanDefinitionException` : aucun bean de type `IAirZoneService` (équivalent de `IContratService`).
Causes probables côté `AirZoneServiceImpl` : l'annotation `@Service` a été oubliée, ou la classe est placée en dehors
du package racine `tn.esprit.air_et_sante` (le scan ne la voit pas).
Correction : ajouter `@Service` et vérifier que la classe est dans un sous-package de `tn.esprit.air_et_sante`.

**Message 2** — `NoUniqueBeanDefinitionException` : deux beans du même type (`emailNotificateur`, `smsNotificateur`).
Corrections possibles :
1. `@Primary` posé **sur la classe** de l'implémentation à utiliser par défaut ;
2. `@Qualifier("emailNotificateur")` posé **sur le paramètre du constructeur** du contrôleur, pour choisir explicitement.

**Message 3** — Dépendance circulaire (`BeanCurrentlyInCreationException`) entre deux services.
Avec l'injection par constructeur, chaque bean doit recevoir l'autre **avant** d'exister : aucun des deux ne peut être
créé en premier, donc le cycle est impossible à contourner.
Piste : revoir la conception, par exemple extraire la responsabilité commune dans un troisième service dont les deux
dépendent, ou faire dépendre un service directement du repository plutôt que de l'autre service.

## D. Anomalies SonarQube for IDE (étape 4)

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut (`@Autowired` sur un champ) dans un service (expérience de l'étape 2) | à relever dans le panneau (S6813 annoncée) : l'injection par attribut cache la dépendance et empêche le champ `final`. | Retour à l'injection par constructeur (`@RequiredArgsConstructor` + champ `final`). |
| `throw new RuntimeException(...)` dans `findById` (expérience de l'étape 4) | à relever dans le panneau (S112 annoncée) : lever une exception générique empêche l'appelant de distinguer les erreurs. | Retour à `ResourceNotFoundException`. |
| à compléter après l'analyse | à compléter | à compléter |

## E. Questions de compréhension

1. **Où est le `new` de `AirZoneServiceImpl` et qui l'exécute ?** Il n'y en a aucun dans notre code. C'est le conteneur
   Spring qui instancie la classe au démarrage, après l'avoir détectée par le scan grâce à `@Service`, en appelant son
   constructeur (généré par Lombok) et en lui passant le proxy de `IAirZoneRepository`.
2. **Pourquoi un contrôleur dépendra-t-il de `IAirZoneService` et non de `AirZoneServiceImpl` ?** Pour dépendre d'une
   abstraction : on peut changer d'implémentation (ou en fournir une de test) sans modifier le contrôleur. Le couplage
   reste faible.
3. **Pourquoi un service singleton doit-il rester sans état ?** Une seule instance est partagée par toutes les requêtes et
   tous les threads. Exemple de bug : un attribut `zoneCourante` dans `AirZoneServiceImpl`, modifié par deux requêtes
   simultanées ; la requête A lirait la zone fixée par la requête B et enregistrerait un relevé dans la mauvaise zone.
4. **Pourquoi `update` charge-t-il l'entité existante avant `save` ?** Passer directement l'objet reçu à `save`
   déclencherait un `merge` qui écraserait toutes les colonnes (les champs absents deviendraient `null`) et pourrait vider
   les collections associées (par exemple les relevés d'une zone). En chargeant puis en recopiant, on ne modifie que les
   attributs voulus et on vérifie au passage que l'élément existe.
5. **`@Component` vs `@Bean` ; `@Primary` vs `@Qualifier` ; lecture seule.** `@Component` (et `@Service`…) se pose sur une
   classe détectée par le scan ; `@Bean` se pose sur une méthode d'une classe `@Configuration` pour déclarer
   explicitement un bean (classe d'une bibliothèque qu'on ne peut pas annoter, construction particulière).
   `@Primary` désigne sur la classe le candidat par défaut ; `@Qualifier` choisit explicitement un bean au point
   d'injection. Dans AutoLoc, `IPaiementService` est en lecture seule car un paiement n'existe qu'à travers son contrat
   (composition) : on le crée ou le supprime via le Contrat. Dans Air & Santé, aucune entité n'est une composition de ce
   type, donc les six services exposent le CRUD complet.
