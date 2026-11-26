# MS1 — Utilisateurs & Santé (Django REST + SQLite)

Port **8081**, inscrit sur Eureka (`http://localhost:8761/eureka`) sous le nom `users`.

## Correspondance avec Air & Santé (Spring Boot)

| Spring Boot (Air & Santé) | Django (Users) |
|---|---|
| `application.properties` | `users_service/settings.py` |
| `domain/` entités JPA + `@Enumerated(STRING)` | `users/models.py` + `users/enums.py` (TextChoices) |
| `repository/I...Repository` | `users/repositories.py` |
| `service/I...Service` + `impl/...ServiceImpl` | `users/services.py` |
| `ResourceNotFoundException` + `GlobalExceptionHandler` | `users/exceptions.py` (404 / 400, corps `statut, message, horodatage`) |
| `@JsonIgnore` sur les listes inverses | `users/serializers.py` (listes inverses non exposées) |
| `web/controller/...Controller` + SpringDoc | `users/views.py` + drf-spectacular |
| `context-path=/api` | `users_service/urls.py` |
| tests JUnit | `users/tests/` |

Base de données : SQLite (fichier `users_db.sqlite3`, embarqué, aucun serveur à installer).

## Installation (une seule fois)

```powershell
cd Backend\Users
python -m venv .venv
.venv\Scripts\python.exe -m pip install -r requirements.txt
.venv\Scripts\python.exe manage.py migrate
.venv\Scripts\python.exe manage.py createsuperuser
```

## Lancer

```powershell
.venv\Scripts\python.exe manage.py runserver 8081
```

Sans Eureka : `$env:EUREKA_CLIENT_ENABLED="false"` avant la commande.

- Swagger UI : http://localhost:8081/api/swagger-ui.html
- OpenAPI : http://localhost:8081/api/v3/api-docs
- Administration (voir les tables) : http://localhost:8081/admin/

## Tests

```powershell
.venv\Scripts\python.exe manage.py test users -v 2
```

## Authentification (JWT)

| Route | Accès | Rôle |
|---|---|---|
| `POST /api/auth/register` | public | crée un compte (rôle CITOYEN) |
| `POST /api/auth/login` | public | retourne `{access, refresh}` |
| `POST /api/auth/refresh` | public | nouveau jeton d'accès à partir du `refresh` |
| `POST /api/auth/logout` | public | révoque le `refresh` (table `refresh_tokens`, `revoque = true`) |
| `GET /api/auth/me` | connecté | l'utilisateur du jeton |

Toutes les autres routes exigent l'en-tête `Authorization: Bearer <access>` (dans Swagger : bouton **Authorize**).

Jeton d'accès : HS256, signé avec la variable d'environnement `JWT_SECRET` (à partager avec la Gateway
et les services Spring), durée 15 min. Contenu : `userId` (nombre), `username`, `role`, `exp`, `token_type`.
Le refresh dure 7 jours.

Droits : un utilisateur ne voit et ne modifie que ses propres données (profil, préférences, profil santé,
sensibilités, clés API, demandes de rôle) et ne peut pas changer son `statut`. Un ADMIN accède à tout,
gère les rôles et approuve les demandes de rôle (l'utilisateur reçoit alors le rôle demandé).
Réponses : 401 sans jeton ou jeton invalide, 403 action interdite.

Le JSON est en camelCase (`idUser`, `dateInscription`), comme les services Spring.

## Endpoints

`/api/roles`, `/api/users`, `/api/role-requests`, `/api/user-preferences`, `/api/health-profiles`,
`/api/sensitivities`, `/api/api-keys`, `/api/refresh-tokens` — chacun avec GET (liste), GET `/{id}`,
POST (201), PUT `/{id}`, DELETE `/{id}` (204). Ressource absente : 404 ; règle métier : 400 ;
suppression d'un parent qui a encore des enfants : 409.

Supprimer un utilisateur supprime aussi ses préférences, ses clés API et ses jetons de rafraîchissement.
Ses demandes de rôle et son profil santé (avec ses sensibilités) doivent être supprimés avant (sinon 409).
