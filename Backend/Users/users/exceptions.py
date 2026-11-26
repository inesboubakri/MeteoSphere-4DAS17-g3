"""Exceptions métier et gestionnaire global (équivalent de ResourceNotFoundException + GlobalExceptionHandler).

- ResourceNotFoundException -> 404
- ValueError (règle métier, équivalent de IllegalArgumentException) -> 400
Le corps de la réponse est toujours : {statut, message, horodatage}.
"""
from django.db import IntegrityError
from django.db.models import ProtectedError
from django.utils import timezone
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import exception_handler


class ResourceNotFoundException(Exception):
    def __init__(self, ressource, entity_id):
        super().__init__(f"{ressource} introuvable avec l'identifiant {entity_id}")


def construire_reponse(statut, message):
    corps = {
        "statut": statut,
        "message": message,
        "horodatage": timezone.localtime().isoformat(),
    }
    return Response(corps, status=statut)


def global_exception_handler(exc, context):
    if isinstance(exc, ResourceNotFoundException):
        return construire_reponse(status.HTTP_404_NOT_FOUND, str(exc))
    if isinstance(exc, ValueError):
        return construire_reponse(status.HTTP_400_BAD_REQUEST, str(exc))
    if isinstance(exc, IntegrityError):
        return construire_reponse(
            status.HTTP_409_CONFLICT,
            "Conflit : cette ressource existe déjà (par exemple un seul profil santé par utilisateur)",
        )
    if isinstance(exc, ProtectedError):
        return construire_reponse(
            status.HTTP_409_CONFLICT,
            "Suppression impossible : des éléments liés existent encore",
        )
    # Erreurs propres à DRF (401 jeton absent ou invalide, 403 accès refusé, 400 JSON invalide,
    # valeur d'énumération inconnue...) : remises au même format {statut, message, horodatage}
    response = exception_handler(exc, context)
    if response is None:
        return None
    message = response.data.get("detail", response.data) if isinstance(response.data, dict) else response.data
    reponse = construire_reponse(response.status_code, message)
    for entete, valeur in response.items():  # garde WWW-Authenticate sur les 401
        reponse[entete] = valeur
    return reponse
