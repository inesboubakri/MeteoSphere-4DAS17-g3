"""Conversion JSON <-> entités (rôle de Jackson en Spring).

- Les listes côté inverse (role.users, user.role_requests, ...) ne sont pas déclarées : elles
  ne sont ni lues ni écrites (équivalent de @JsonIgnore), ce qui évite les boucles.
- Le côté propriétaire désigne son parent par son identifiant : {"user": 1}.
- L'identifiant est accepté en entrée pour que le service puisse refuser un POST qui en contient un (400).
- password_hash est en écriture seule : il n'apparaît jamais dans les réponses.
"""
from rest_framework import serializers

from users.models import ApiKey, HealthProfile, RefreshToken, Role, RoleRequest, Sensitivity, User, UserPreferences


def identifiant_modifiable(nom_id):
    return {nom_id: {"read_only": False, "required": False, "validators": []}}


class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = ["id_role", "nom"]
        extra_kwargs = identifiant_modifiable("id_role")


class UserSerializer(serializers.ModelSerializer):
    # Mot de passe en clair à l'entrée uniquement ; Django n'enregistre que son empreinte
    password = serializers.CharField(write_only=True, required=False, allow_blank=True)

    class Meta:
        model = User
        fields = ["id_user", "username", "email", "password", "date_inscription", "statut",
                  "ville_residence", "role"]
        extra_kwargs = {
            **identifiant_modifiable("id_user"),
            "date_inscription": {"read_only": True},
            # unicité contrôlée par le service (message 400 en français)
            "username": {"validators": []},
            "email": {"validators": []},
        }


# ---------------------------------------------------------------- authentification

class RegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ["username", "email", "password", "ville_residence"]
        extra_kwargs = {"username": {"validators": []}, "email": {"validators": []}}


class LoginSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField(write_only=True)


class TokenPairSerializer(serializers.Serializer):
    access = serializers.CharField()
    refresh = serializers.CharField()


class RefreshRequestSerializer(serializers.Serializer):
    refresh = serializers.CharField()


class AccessTokenSerializer(serializers.Serializer):
    access = serializers.CharField()


class RoleRequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = RoleRequest
        fields = ["id_request", "role_demande", "justificatif", "date_demande", "statut", "user"]
        extra_kwargs = identifiant_modifiable("id_request")


class UserPreferencesSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserPreferences
        fields = ["id_preference", "langue", "unite_temperature", "unite_vent", "zones_favorites",
                  "canaux_notification", "heure_debut_silence", "heure_fin_silence", "user"]
        extra_kwargs = {**identifiant_modifiable("id_preference"), "user": {"validators": []}}


class HealthProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = HealthProfile
        fields = ["id_profile", "situation", "score_sensibilite", "partage_autorise", "date_mise_a_jour", "user"]
        extra_kwargs = {**identifiant_modifiable("id_profile"), "user": {"validators": []}}


class SensitivitySerializer(serializers.ModelSerializer):
    class Meta:
        model = Sensitivity
        fields = ["id_sensitivity", "type", "niveau", "facteurs_declencheurs", "health_profile"]
        extra_kwargs = identifiant_modifiable("id_sensitivity")


class ApiKeySerializer(serializers.ModelSerializer):
    class Meta:
        model = ApiKey
        fields = ["id_api_key", "cle", "date_creation", "date_expiration", "user"]
        extra_kwargs = identifiant_modifiable("id_api_key")


class RefreshTokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = RefreshToken
        fields = ["id_token", "token", "date_expiration", "revoque", "user"]
        extra_kwargs = identifiant_modifiable("id_token")
