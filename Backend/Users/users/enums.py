"""Énumérations du diagramme de classes (stockées en texte, équivalent de @Enumerated(EnumType.STRING))."""
from django.db import models


class RoleName(models.TextChoices):
    VISITEUR = "VISITEUR"
    CITOYEN = "CITOYEN"
    METEOROLOGUE = "METEOROLOGUE"
    ADMIN = "ADMIN"


class StatutCompte(models.TextChoices):
    ACTIF = "ACTIF"
    SUSPENDU = "SUSPENDU"


class StatutDemande(models.TextChoices):
    EN_ATTENTE = "EN_ATTENTE"
    APPROUVEE = "APPROUVEE"
    REFUSEE = "REFUSEE"


class UniteTemperature(models.TextChoices):
    CELSIUS = "CELSIUS"
    FAHRENHEIT = "FAHRENHEIT"


class TypeSensibilite(models.TextChoices):
    ALLERGIE_POLLEN = "ALLERGIE_POLLEN"
    ALLERGIE_POUSSIERE = "ALLERGIE_POUSSIERE"
    ASTHME = "ASTHME"
    CARDIAQUE = "CARDIAQUE"
    CHALEUR = "CHALEUR"
    FROID = "FROID"
    UV = "UV"
    MIGRAINE = "MIGRAINE"


class NiveauSensibilite(models.TextChoices):
    LEGER = "LEGER"
    MODERE = "MODERE"
    SEVERE = "SEVERE"


class SituationParticuliere(models.TextChoices):
    AUCUNE = "AUCUNE"
    FEMME_ENCEINTE = "FEMME_ENCEINTE"
    ENFANT = "ENFANT"
    PERSONNE_AGEE = "PERSONNE_AGEE"
    SPORTIF = "SPORTIF"
