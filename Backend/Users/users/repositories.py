"""Couche d'accès aux données (équivalent des interfaces I...Repository extends JpaRepository).

Chaque repository expose les mêmes opérations que JpaRepository : save, find_by_id, find_all,
exists_by_id, delete_by_id, count. Il s'appuie sur le manager Django Model.objects.
"""
from users.models import ApiKey, HealthProfile, RefreshToken, Role, RoleRequest, Sensitivity, User, UserPreferences


class BaseRepository:
    model = None

    def save(self, entity):
        entity.save()
        return entity

    def find_by_id(self, entity_id):
        """Retourne l'entité ou None (équivalent de Optional)."""
        return self.model.objects.filter(pk=entity_id).first()

    def find_all(self):
        return list(self.model.objects.all())

    def find_all_by(self, **criteres):
        """Équivalent d'une méthode dérivée findAllByUser(...) de Spring Data."""
        return list(self.model.objects.filter(**criteres))

    def exists_by_id(self, entity_id):
        return self.model.objects.filter(pk=entity_id).exists()

    def delete_by_id(self, entity_id):
        self.model.objects.filter(pk=entity_id).delete()

    def count(self):
        return self.model.objects.count()


class IRoleRepository(BaseRepository):
    model = Role


class IUserRepository(BaseRepository):
    model = User


class IRoleRequestRepository(BaseRepository):
    model = RoleRequest


class IUserPreferencesRepository(BaseRepository):
    model = UserPreferences


class IHealthProfileRepository(BaseRepository):
    model = HealthProfile


class ISensitivityRepository(BaseRepository):
    model = Sensitivity


class IApiKeyRepository(BaseRepository):
    model = ApiKey


class IRefreshTokenRepository(BaseRepository):
    model = RefreshToken
