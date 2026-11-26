"""Contrôleurs REST (équivalent des @RestController + annotations SpringDoc).

Chaque ressource expose le CRUD complet :
GET liste (200), GET /{id} (200/404), POST (201/400), PUT /{id} (200/400/404), DELETE /{id} (204/404).
Le contrôleur délègue au service, qui lève ResourceNotFoundException (404) ou ValueError (400).

Sécurité (jeton JWT obligatoire, sauf /api/auth/register, /login, /refresh, /logout) :
- 401 : pas de jeton, ou jeton invalide/expiré
- 403 : jeton valide mais action non autorisée
Règle générale : un utilisateur ne voit et ne modifie que SES données ; un ADMIN accède à tout.
"""
from dataclasses import dataclass, field

from drf_spectacular.utils import OpenApiParameter, OpenApiResponse, extend_schema
from rest_framework import status
from rest_framework.exceptions import PermissionDenied
from rest_framework.generics import GenericAPIView
from rest_framework.permissions import AllowAny
from rest_framework.response import Response

from users import serializers as s
from users import services
from users.auth import AuthServiceImpl

ID_DESC = "Identifiant de la ressource"
NOT_FOUND = "Ressource introuvable"
INVALID = "Données invalides"
FORBIDDEN = "Accès refusé"
UNAUTHORIZED = "Jeton absent, invalide ou expiré"

ADMIN = "admin"        # réservé aux administrateurs
OWNER = "owner"        # le propriétaire de la ressource, ou un administrateur
CONNECTE = "connecte"  # tout utilisateur connecté


@dataclass
class Politique:
    """Qui a le droit de faire quoi sur une ressource."""
    lecture: str = OWNER
    creation: str = OWNER
    modification: str = OWNER
    suppression: str = OWNER
    # chemin de la ressource vers son propriétaire ("self" = la ressource EST l'utilisateur)
    proprietaire: str = "user"
    # champs qu'un non-administrateur ne peut pas changer
    champs_admin: tuple = field(default_factory=tuple)


def est_admin(request):
    return request.user.est_admin


def proprietaire_de(entity, chemin):
    if chemin == "self":
        return entity
    for attribut in chemin.split("__"):
        entity = getattr(entity, attribut, None) if entity is not None else None
    return entity


def autoriser(request, regle, entity=None, politique=None):
    if regle == CONNECTE or est_admin(request):
        return
    if regle == ADMIN:
        raise PermissionDenied("Action réservée aux administrateurs")
    if entity is not None:
        owner = proprietaire_de(entity, politique.proprietaire)
        if owner is None or owner.pk != request.user.pk:
            raise PermissionDenied("Cette ressource appartient à un autre utilisateur")


def build_controllers(tag, libelle, service, ser, politique):
    """Construit le contrôleur liste/création et le contrôleur détail d'une ressource."""
    id_param = OpenApiParameter("id", int, OpenApiParameter.PATH, description=ID_DESC)
    erreurs = {401: OpenApiResponse(description=UNAUTHORIZED), 403: OpenApiResponse(description=FORBIDDEN)}

    class CollectionController(GenericAPIView):
        serializer_class = ser

        @extend_schema(tags=[tag], summary=f"Lister : {libelle}",
                       responses={200: ser(many=True), **erreurs})
        def get(self, request):
            if politique.lecture == ADMIN:
                autoriser(request, ADMIN)
            if politique.lecture == CONNECTE or est_admin(request):
                entites = service.find_all()
            elif politique.proprietaire == "self":
                entites = [request.user]
            else:
                entites = service.find_all_by(**{politique.proprietaire: request.user})
            return Response(ser(entites, many=True).data)

        @extend_schema(tags=[tag], summary=f"Créer : {libelle}", request=ser,
                       responses={201: ser, 400: OpenApiResponse(description=INVALID), **erreurs})
        def post(self, request):
            autoriser(request, politique.creation if politique.creation != OWNER else CONNECTE)
            serializer = ser(data=request.data)
            serializer.is_valid(raise_exception=True)
            entity = ser.Meta.model(**serializer.validated_data)
            if politique.creation == OWNER and not est_admin(request):
                if politique.proprietaire == "user":
                    entity.user = request.user  # on crée toujours pour soi-même
                else:
                    autoriser(request, OWNER, entity, politique)
            created = service.create(entity)
            return Response(ser(created).data, status=status.HTTP_201_CREATED)

    class DetailController(GenericAPIView):
        serializer_class = ser

        @extend_schema(tags=[tag], summary=f"Consulter : {libelle}", parameters=[id_param],
                       responses={200: ser, 404: OpenApiResponse(description=NOT_FOUND), **erreurs})
        def get(self, request, id):
            entity = service.find_by_id(id)
            autoriser(request, politique.lecture, entity, politique)
            return Response(ser(entity).data)

        @extend_schema(tags=[tag], summary=f"Modifier : {libelle}", parameters=[id_param], request=ser,
                       responses={200: ser, 400: OpenApiResponse(description=INVALID),
                                  404: OpenApiResponse(description=NOT_FOUND), **erreurs})
        def put(self, request, id):
            existant = service.find_by_id(id)
            autoriser(request, politique.modification, existant, politique)
            serializer = ser(data=request.data)
            serializer.is_valid(raise_exception=True)
            data = dict(serializer.validated_data)
            data.pop(ser.Meta.model._meta.pk.name, None)
            entity = ser.Meta.model(**data)
            if not est_admin(request):
                for champ in politique.champs_admin:
                    setattr(entity, champ, getattr(existant, champ))
            updated = service.update(id, entity)
            return Response(ser(updated).data)

        @extend_schema(tags=[tag], summary=f"Supprimer : {libelle}", parameters=[id_param],
                       responses={204: None, 404: OpenApiResponse(description=NOT_FOUND), **erreurs})
        def delete(self, request, id):
            entity = service.find_by_id(id)
            autoriser(request, politique.suppression, entity, politique)
            service.delete_by_id(id)
            return Response(status=status.HTTP_204_NO_CONTENT)

    name = ser.Meta.model.__name__
    CollectionController.__name__ = f"{name}CollectionController"
    DetailController.__name__ = f"{name}DetailController"
    return CollectionController, DetailController


user_service = services.UserServiceImpl()

RoleController = build_controllers(
    "Rôles", "rôle", services.RoleServiceImpl(), s.RoleSerializer,
    Politique(lecture=CONNECTE, creation=ADMIN, modification=ADMIN, suppression=ADMIN),
)
UserController = build_controllers(
    "Utilisateurs", "utilisateur", user_service, s.UserSerializer,
    Politique(lecture=OWNER, creation=ADMIN, modification=OWNER, suppression=ADMIN,
              proprietaire="self", champs_admin=("statut",)),
)
RoleRequestController = build_controllers(
    "Demandes de rôle", "demande de rôle", services.RoleRequestServiceImpl(), s.RoleRequestSerializer,
    Politique(modification=ADMIN),  # seul un ADMIN approuve ou refuse
)
UserPreferencesController = build_controllers(
    "Préférences", "préférences", services.UserPreferencesServiceImpl(), s.UserPreferencesSerializer,
    Politique(),
)
HealthProfileController = build_controllers(
    "Profils santé", "profil santé", services.HealthProfileServiceImpl(), s.HealthProfileSerializer,
    Politique(),
)
SensitivityController = build_controllers(
    "Sensibilités", "sensibilité", services.SensitivityServiceImpl(), s.SensitivitySerializer,
    Politique(proprietaire="health_profile__user"),
)
ApiKeyController = build_controllers(
    "Clés API", "clé API", services.ApiKeyServiceImpl(), s.ApiKeySerializer,
    Politique(),
)
RefreshTokenController = build_controllers(
    "Jetons", "jeton de rafraîchissement", services.RefreshTokenServiceImpl(), s.RefreshTokenSerializer,
    Politique(lecture=ADMIN, creation=ADMIN, modification=ADMIN, suppression=ADMIN),
)


# ---------------------------------------------------------------- authentification

auth_service = AuthServiceImpl()
AUTH_TAG = "Authentification"


class PublicView(GenericAPIView):
    authentication_classes = []
    permission_classes = [AllowAny]

    def get_authenticate_header(self, request):
        # sans cela, DRF transforme les 401 des routes publiques en 403
        return 'Bearer realm="api"'


class RegisterView(PublicView):
    serializer_class = s.RegisterSerializer

    @extend_schema(tags=[AUTH_TAG], summary="Créer un compte (rôle CITOYEN)", request=s.RegisterSerializer,
                   responses={201: s.UserSerializer, 400: OpenApiResponse(description=INVALID)})
    def post(self, request):
        serializer = s.RegisterSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = user_service.register(s.User(**serializer.validated_data))
        return Response(s.UserSerializer(user).data, status=status.HTTP_201_CREATED)


class LoginView(PublicView):
    serializer_class = s.LoginSerializer

    @extend_schema(tags=[AUTH_TAG], summary="Se connecter (retourne access et refresh)", request=s.LoginSerializer,
                   responses={200: s.TokenPairSerializer, 401: OpenApiResponse(description=UNAUTHORIZED)})
    def post(self, request):
        serializer = s.LoginSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(auth_service.login(**serializer.validated_data))


class RefreshView(PublicView):
    serializer_class = s.RefreshRequestSerializer

    @extend_schema(tags=[AUTH_TAG], summary="Obtenir un nouveau jeton d'accès", request=s.RefreshRequestSerializer,
                   responses={200: s.AccessTokenSerializer, 401: OpenApiResponse(description=UNAUTHORIZED)})
    def post(self, request):
        serializer = s.RefreshRequestSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(auth_service.refresh(serializer.validated_data["refresh"]))


class LogoutView(PublicView):
    serializer_class = s.RefreshRequestSerializer

    @extend_schema(tags=[AUTH_TAG], summary="Se déconnecter (révoque le jeton de rafraîchissement)",
                   request=s.RefreshRequestSerializer,
                   responses={204: None, 401: OpenApiResponse(description=UNAUTHORIZED)})
    def post(self, request):
        serializer = s.RefreshRequestSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        auth_service.logout(serializer.validated_data["refresh"])
        return Response(status=status.HTTP_204_NO_CONTENT)


class MeView(GenericAPIView):
    serializer_class = s.UserSerializer

    @extend_schema(tags=[AUTH_TAG], summary="Utilisateur connecté",
                   responses={200: s.UserSerializer, 401: OpenApiResponse(description=UNAUTHORIZED)})
    def get(self, request):
        return Response(s.UserSerializer(request.user).data)
