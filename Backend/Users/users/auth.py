"""Authentification JWT du service (inscription, connexion, rafraîchissement, déconnexion).

Le jeton d'accès est signé en HS256 avec la clé JWT_SECRET partagée avec la Gateway et les
services Spring. Il contient : userId, username, role (+ exp, iat, jti, token_type).
Chaque jeton de rafraîchissement émis est aussi enregistré dans la table refresh_tokens
du diagramme de classes : la déconnexion le marque revoque = true, et il ne peut plus servir.
"""
from datetime import datetime, timezone

from django.contrib.auth import authenticate
from django.db import transaction
from rest_framework.exceptions import AuthenticationFailed
from rest_framework_simplejwt.exceptions import TokenError
from rest_framework_simplejwt.tokens import RefreshToken as JwtRefreshToken

from users.enums import StatutCompte
from users.models import RefreshToken as RefreshTokenEntity
from users.models import User
from users.repositories import IRefreshTokenRepository, IUserRepository

IDENTIFIANTS_INVALIDES = "Nom d'utilisateur ou mot de passe incorrect"
JETON_INVALIDE = "Jeton de rafraîchissement invalide, expiré ou révoqué"


class MeteoRefreshToken(JwtRefreshToken):
    """Ajoute username et role au jeton (le jeton d'accès dérivé reprend ces informations)."""

    @classmethod
    def for_user(cls, user):
        token = super().for_user(user)
        token["userId"] = user.id_user  # nombre (et non texte), lisible directement en Long côté Spring
        token["username"] = user.username
        token["role"] = user.role.nom if user.role else None
        return token


class AuthServiceImpl:
    def __init__(self, user_repository=None, refresh_token_repository=None):
        self.user_repository = user_repository or IUserRepository()
        self.refresh_token_repository = refresh_token_repository or IRefreshTokenRepository()

    @transaction.atomic
    def login(self, username, password):
        user = authenticate(username=username, password=password)
        if user is None:
            raise AuthenticationFailed(IDENTIFIANTS_INVALIDES)
        if user.statut == StatutCompte.SUSPENDU:
            raise AuthenticationFailed("Ce compte est suspendu")
        refresh = MeteoRefreshToken.for_user(user)
        self.refresh_token_repository.save(RefreshTokenEntity(
            token=str(refresh),
            date_expiration=datetime.fromtimestamp(refresh["exp"], tz=timezone.utc),
            revoque=False,
            user=user,
        ))
        return {"access": str(refresh.access_token), "refresh": str(refresh)}

    def refresh(self, refresh_token):
        entite = self._jeton_actif(refresh_token)
        user = entite.user
        if user.statut == StatutCompte.SUSPENDU:
            raise AuthenticationFailed("Ce compte est suspendu")
        # nouveau jeton d'accès construit à partir de l'utilisateur actuel (rôle à jour)
        return {"access": str(MeteoRefreshToken.for_user(user).access_token)}

    @transaction.atomic
    def logout(self, refresh_token):
        entite = self._jeton_actif(refresh_token)
        entite.revoque = True
        self.refresh_token_repository.save(entite)

    def _jeton_actif(self, refresh_token):
        try:
            MeteoRefreshToken(refresh_token)  # vérifie la signature et l'expiration
        except TokenError:
            raise AuthenticationFailed(JETON_INVALIDE)
        entite = RefreshTokenEntity.objects.select_related("user").filter(token=refresh_token).first()
        if entite is None or entite.revoque:
            raise AuthenticationFailed(JETON_INVALIDE)
        return entite


__all__ = ["AuthServiceImpl", "MeteoRefreshToken", "User"]
