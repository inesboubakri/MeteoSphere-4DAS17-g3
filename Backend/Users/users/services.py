"""Couche service : interfaces I...Service et implémentations ...ServiceImpl.

Chaque implémentation reçoit son repository dans le constructeur (équivalent de @RequiredArgsConstructor),
porte les règles métier (verifier_regles) et lève :
- ValueError -> 400 (règle métier violée, identifiant fourni à la création)
- ResourceNotFoundException -> 404
Les méthodes d'écriture s'exécutent dans une transaction (équivalent de @Transactional).
"""
from abc import ABC, abstractmethod

from django.contrib.auth.hashers import identify_hasher, make_password
from django.db import transaction
from django.utils import timezone

from users.enums import RoleName, StatutCompte, StatutDemande
from users.exceptions import ResourceNotFoundException
from users.models import Role
from users.repositories import (
    IApiKeyRepository,
    IHealthProfileRepository,
    IRefreshTokenRepository,
    IRoleRepository,
    IRoleRequestRepository,
    ISensitivityRepository,
    IUserPreferencesRepository,
    IUserRepository,
)


# ---------------------------------------------------------------- interfaces

class ICrudService(ABC):
    @abstractmethod
    def create(self, entity): ...

    @abstractmethod
    def find_by_id(self, entity_id): ...

    @abstractmethod
    def find_all(self): ...

    @abstractmethod
    def update(self, entity_id, entity): ...

    @abstractmethod
    def delete_by_id(self, entity_id): ...


class IRoleService(ICrudService, ABC): ...
class IUserService(ICrudService, ABC): ...
class IRoleRequestService(ICrudService, ABC): ...
class IUserPreferencesService(ICrudService, ABC): ...
class IHealthProfileService(ICrudService, ABC): ...
class ISensitivityService(ICrudService, ABC): ...
class IApiKeyService(ICrudService, ABC): ...
class IRefreshTokenService(ICrudService, ABC): ...


# ---------------------------------------------------------------- base commune

class BaseCrudService(ICrudService):
    libelle = ""              # nom de la ressource dans les messages d'erreur
    champs_modifiables = ()   # attributs recopiés par update()

    def __init__(self, repository):
        self.repository = repository

    def verifier_regles(self, entity):
        """Règles métier propres à chaque entité (redéfinie dans les sous-classes)."""

    @transaction.atomic
    def create(self, entity):
        if entity.pk is not None:
            raise ValueError(f"Un nouvel objet {self.libelle} ne doit pas avoir d'identifiant")
        self.verifier_regles(entity)
        return self.repository.save(entity)

    def find_by_id(self, entity_id):
        entity = self.repository.find_by_id(entity_id)
        if entity is None:
            raise ResourceNotFoundException(self.libelle, entity_id)
        return entity

    def find_all(self):
        return self.repository.find_all()

    def find_all_by(self, **criteres):
        return self.repository.find_all_by(**criteres)

    @transaction.atomic
    def update(self, entity_id, entity):
        self.verifier_regles(entity)
        existant = self.find_by_id(entity_id)
        for champ in self.champs_modifiables:
            setattr(existant, champ, getattr(entity, champ))
        return self.repository.save(existant)

    @transaction.atomic
    def delete_by_id(self, entity_id):
        if not self.repository.exists_by_id(entity_id):
            raise ResourceNotFoundException(self.libelle, entity_id)
        self.repository.delete_by_id(entity_id)


def est_vide(valeur):
    return valeur is None or str(valeur).strip() == ""


# ---------------------------------------------------------------- implémentations

class RoleServiceImpl(BaseCrudService, IRoleService):
    libelle = "Rôle"
    champs_modifiables = ("nom",)

    def __init__(self, role_repository=None):
        super().__init__(role_repository or IRoleRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.nom):
            raise ValueError("Le nom du rôle est obligatoire")


class UserServiceImpl(BaseCrudService, IUserService):
    libelle = "Utilisateur"
    champs_modifiables = ("username", "email", "statut", "ville_residence")

    def __init__(self, user_repository=None, role_repository=None):
        super().__init__(user_repository or IUserRepository())
        self.role_repository = role_repository or IRoleRepository()

    def verifier_regles(self, entity):
        if est_vide(entity.username):
            raise ValueError("Le nom d'utilisateur est obligatoire")
        if est_vide(entity.email) or "@" not in entity.email:
            raise ValueError("L'email est obligatoire et doit contenir @")
        doublon = self.repository.find_all_by(email=entity.email)
        if any(u.pk != entity.pk for u in doublon):
            raise ValueError("Cet email est déjà utilisé")
        doublon = self.repository.find_all_by(username=entity.username)
        if any(u.pk != entity.pk for u in doublon):
            raise ValueError("Ce nom d'utilisateur est déjà utilisé")

    @transaction.atomic
    def create(self, entity):
        if entity.pk is not None:
            raise ValueError(f"Un nouvel objet {self.libelle} ne doit pas avoir d'identifiant")
        self.verifier_regles(entity)
        if est_vide(entity.password):
            raise ValueError("Le mot de passe est obligatoire")
        entity.password = self.hacher(entity.password)
        return self.repository.save(entity)

    @transaction.atomic
    def register(self, entity):
        """Inscription publique : le nouveau compte reçoit toujours le rôle CITOYEN."""
        entity.role = Role.objects.get_or_create(nom=RoleName.CITOYEN)[0]
        entity.statut = StatutCompte.ACTIF
        return self.create(entity)

    @transaction.atomic
    def update(self, entity_id, entity):
        existant = self.find_by_id(entity_id)
        entity.pk = existant.pk  # pour que les contrôles d'unicité ignorent l'utilisateur lui-même
        self.verifier_regles(entity)
        for champ in self.champs_modifiables:
            setattr(existant, champ, getattr(entity, champ))
        if not est_vide(entity.password):
            existant.password = self.hacher(entity.password)
        return self.repository.save(existant)

    @staticmethod
    def hacher(mot_de_passe):
        """Le mot de passe n'est jamais stocké en clair : on garde son empreinte (passwordHash)."""
        try:
            identify_hasher(mot_de_passe)
            return mot_de_passe  # déjà haché
        except ValueError:
            return make_password(mot_de_passe)


class RoleRequestServiceImpl(BaseCrudService, IRoleRequestService):
    libelle = "Demande de rôle"
    champs_modifiables = ("role_demande", "justificatif", "date_demande", "statut")

    def __init__(self, role_request_repository=None):
        super().__init__(role_request_repository or IRoleRequestRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.role_demande):
            raise ValueError("Le rôle demandé est obligatoire")

    @transaction.atomic
    def create(self, entity):
        entity.statut = StatutDemande.EN_ATTENTE
        if entity.date_demande is None:
            entity.date_demande = timezone.now()
        return super().create(entity)

    @transaction.atomic
    def update(self, entity_id, entity):
        """Quand un ADMIN approuve la demande, l'utilisateur reçoit le rôle demandé."""
        demande = super().update(entity_id, entity)
        if demande.statut == StatutDemande.APPROUVEE and demande.user is not None:
            demande.user.role = Role.objects.get_or_create(nom=demande.role_demande)[0]
            demande.user.save()
        return demande


class UserPreferencesServiceImpl(BaseCrudService, IUserPreferencesService):
    libelle = "Préférences"
    champs_modifiables = ("langue", "unite_temperature", "unite_vent", "zones_favorites",
                          "canaux_notification", "heure_debut_silence", "heure_fin_silence")

    def __init__(self, user_preferences_repository=None):
        super().__init__(user_preferences_repository or IUserPreferencesRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.langue):
            raise ValueError("La langue est obligatoire")


class HealthProfileServiceImpl(BaseCrudService, IHealthProfileService):
    libelle = "Profil santé"
    champs_modifiables = ("situation", "score_sensibilite", "partage_autorise", "date_mise_a_jour")

    def __init__(self, health_profile_repository=None):
        super().__init__(health_profile_repository or IHealthProfileRepository())

    def verifier_regles(self, entity):
        score = entity.score_sensibilite
        if score is not None and not 0 <= score <= 100:
            raise ValueError("Le score de sensibilité doit être compris entre 0 et 100")


class SensitivityServiceImpl(BaseCrudService, ISensitivityService):
    libelle = "Sensibilité"
    champs_modifiables = ("type", "niveau", "facteurs_declencheurs")

    def __init__(self, sensitivity_repository=None):
        super().__init__(sensitivity_repository or ISensitivityRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.type):
            raise ValueError("Le type de sensibilité est obligatoire")


class ApiKeyServiceImpl(BaseCrudService, IApiKeyService):
    libelle = "Clé API"
    champs_modifiables = ("cle", "date_creation", "date_expiration")

    def __init__(self, api_key_repository=None):
        super().__init__(api_key_repository or IApiKeyRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.cle):
            raise ValueError("La clé est obligatoire")
        if entity.date_creation and entity.date_expiration and entity.date_expiration < entity.date_creation:
            raise ValueError("La date d'expiration doit être postérieure à la date de création")


class RefreshTokenServiceImpl(BaseCrudService, IRefreshTokenService):
    libelle = "Jeton"
    champs_modifiables = ("token", "date_expiration", "revoque")

    def __init__(self, refresh_token_repository=None):
        super().__init__(refresh_token_repository or IRefreshTokenRepository())

    def verifier_regles(self, entity):
        if est_vide(entity.token):
            raise ValueError("Le jeton est obligatoire")
