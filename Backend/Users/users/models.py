"""Entités du microservice Utilisateurs & Santé, d'après le diagramme de classes.

Associations (côté propriétaire = la clé étrangère) :
- User "*" --> "1" Role               : User.role
- User "1" --> "*" RoleRequest        : RoleRequest.user
- User "1" --> "1" UserPreferences    : UserPreferences.user
- User "1" --> "0..1" HealthProfile   : HealthProfile.user
- HealthProfile "1" --> "*" Sensitivity : Sensitivity.health_profile
- User "1" --> "*" ApiKey             : ApiKey.user
- User "1" --> "*" RefreshToken       : RefreshToken.user

Le related_name donne le côté inverse (équivalent de mappedBy) : role.users, user.role_requests, ...
on_delete=PROTECT : on ne peut pas supprimer un parent qui a encore des enfants
(même comportement que la contrainte de clé étrangère en JPA, sans cascade).
Exceptions : préférences, clés API et jetons de rafraîchissement sont supprimés avec
leur utilisateur (on_delete=CASCADE, équivalent de cascade = REMOVE).
"""
from datetime import date

from django.contrib.auth.models import AbstractUser
from django.db import models

from users.enums import (
    NiveauSensibilite,
    RoleName,
    SituationParticuliere,
    StatutCompte,
    StatutDemande,
    TypeSensibilite,
    UniteTemperature,
)


class Role(models.Model):
    id_role = models.BigAutoField(primary_key=True)
    nom = models.CharField(max_length=20, choices=RoleName.choices, null=True, blank=True)

    class Meta:
        db_table = "roles"

    def __str__(self):
        return f"Role {self.id_role} - {self.nom}"


class User(AbstractUser):
    """Utilisateur de la plateforme, utilisé aussi par Django pour l'authentification (AUTH_USER_MODEL).

    AbstractUser fournit déjà username, email et password (le passwordHash du diagramme :
    Django ne stocke que l'empreinte du mot de passe, jamais le mot de passe en clair).
    """
    id_user = models.BigAutoField(primary_key=True)
    email = models.EmailField(unique=True)
    date_inscription = models.DateField(default=date.today)
    statut = models.CharField(max_length=20, choices=StatutCompte.choices, default=StatutCompte.ACTIF)
    ville_residence = models.CharField(max_length=100, null=True, blank=True)
    role = models.ForeignKey(Role, on_delete=models.PROTECT, related_name="users", null=True, blank=True)

    REQUIRED_FIELDS = ["email"]

    class Meta:
        db_table = "users"

    @property
    def est_admin(self):
        return self.is_superuser or (self.role is not None and self.role.nom == RoleName.ADMIN)

    def __str__(self):
        return f"User {self.id_user} - {self.username}"


class RoleRequest(models.Model):
    id_request = models.BigAutoField(primary_key=True)
    role_demande = models.CharField(max_length=20, choices=RoleName.choices, null=True, blank=True)
    justificatif = models.TextField(null=True, blank=True)
    date_demande = models.DateTimeField(null=True, blank=True)
    statut = models.CharField(max_length=20, choices=StatutDemande.choices, null=True, blank=True)
    user = models.ForeignKey(User, on_delete=models.PROTECT, related_name="role_requests", null=True, blank=True)

    class Meta:
        db_table = "role_requests"


class UserPreferences(models.Model):
    id_preference = models.BigAutoField(primary_key=True)
    langue = models.CharField(max_length=10, null=True, blank=True)
    unite_temperature = models.CharField(max_length=20, choices=UniteTemperature.choices, null=True, blank=True)
    unite_vent = models.CharField(max_length=20, null=True, blank=True)
    zones_favorites = models.TextField(null=True, blank=True)
    canaux_notification = models.TextField(null=True, blank=True)
    heure_debut_silence = models.TimeField(null=True, blank=True)
    heure_fin_silence = models.TimeField(null=True, blank=True)
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name="preferences", null=True, blank=True)

    class Meta:
        db_table = "user_preferences"


class HealthProfile(models.Model):
    id_profile = models.BigAutoField(primary_key=True)
    situation = models.CharField(max_length=20, choices=SituationParticuliere.choices, null=True, blank=True)
    score_sensibilite = models.IntegerField(null=True, blank=True)
    partage_autorise = models.BooleanField(null=True, blank=True)
    date_mise_a_jour = models.DateTimeField(null=True, blank=True)
    user = models.OneToOneField(User, on_delete=models.PROTECT, related_name="health_profile", null=True, blank=True)

    class Meta:
        db_table = "health_profiles"


class Sensitivity(models.Model):
    id_sensitivity = models.BigAutoField(primary_key=True)
    type = models.CharField(max_length=30, choices=TypeSensibilite.choices, null=True, blank=True)
    niveau = models.CharField(max_length=20, choices=NiveauSensibilite.choices, null=True, blank=True)
    facteurs_declencheurs = models.TextField(null=True, blank=True)
    health_profile = models.ForeignKey(
        HealthProfile, on_delete=models.PROTECT, related_name="sensitivities", null=True, blank=True
    )

    class Meta:
        db_table = "sensitivities"


class ApiKey(models.Model):
    id_api_key = models.BigAutoField(primary_key=True)
    cle = models.CharField(max_length=255, null=True, blank=True)
    date_creation = models.DateField(null=True, blank=True)
    date_expiration = models.DateField(null=True, blank=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="api_keys", null=True, blank=True)

    class Meta:
        db_table = "api_keys"


class RefreshToken(models.Model):
    id_token = models.BigAutoField(primary_key=True)
    token = models.CharField(max_length=512, null=True, blank=True)
    date_expiration = models.DateTimeField(null=True, blank=True)
    revoque = models.BooleanField(null=True, blank=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="refresh_tokens", null=True, blank=True)

    class Meta:
        db_table = "refresh_tokens"
