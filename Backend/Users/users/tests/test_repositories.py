"""Atelier 3 - Partie 2 : tests des repositories (un test par entité)."""
from datetime import date, datetime, time, timezone

from django.test import TestCase

from users.enums import (
    NiveauSensibilite,
    RoleName,
    SituationParticuliere,
    StatutCompte,
    StatutDemande,
    TypeSensibilite,
    UniteTemperature,
)
from users.models import ApiKey, HealthProfile, RefreshToken, Role, RoleRequest, Sensitivity, User, UserPreferences
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


class RepositoryTests(TestCase):
    def setUp(self):
        self.role_repository = IRoleRepository()
        self.user_repository = IUserRepository()
        self.role = self.role_repository.save(Role(nom=RoleName.CITOYEN))
        self.user = self.user_repository.save(
            User(username="ines", email="ines@meteosphere.tn", statut=StatutCompte.ACTIF,
                 date_inscription=date(2026, 11, 26), ville_residence="Tunis", role=self.role)
        )

    def test_context_loads(self):
        self.assertIsNotNone(self.user_repository)

    def test_role_repository(self):
        repository = self.role_repository
        admin = repository.save(Role(nom=RoleName.ADMIN))
        print("Rôle créé :", admin.id_role)
        self.assertIsNotNone(admin.id_role)
        self.assertEqual(RoleName.ADMIN, repository.find_by_id(admin.id_role).nom)
        self.assertEqual(2, repository.count())
        repository.delete_by_id(admin.id_role)
        self.assertFalse(repository.exists_by_id(admin.id_role))

    def test_user_repository(self):
        repository = self.user_repository
        trouve = repository.find_by_id(self.user.id_user)
        print("Utilisateur trouvé :", trouve.username)
        self.assertEqual("ines", trouve.username)
        self.assertEqual(self.role.id_role, trouve.role.id_role)
        self.assertEqual(1, len(repository.find_all()))
        self.assertEqual(1, self.role.users.count())

    def test_role_request_repository(self):
        repository = IRoleRequestRepository()
        demande = repository.save(
            RoleRequest(role_demande=RoleName.METEOROLOGUE, justificatif="Diplôme INM",
                        date_demande=datetime(2026, 11, 26, 10, 0, tzinfo=timezone.utc),
                        statut=StatutDemande.EN_ATTENTE, user=self.user)
        )
        print("Demande créée :", demande.id_request)
        self.assertTrue(repository.exists_by_id(demande.id_request))
        self.assertEqual(1, self.user.role_requests.count())
        repository.delete_by_id(demande.id_request)
        self.assertEqual(0, repository.count())

    def test_user_preferences_repository(self):
        repository = IUserPreferencesRepository()
        preferences = repository.save(
            UserPreferences(langue="fr", unite_temperature=UniteTemperature.CELSIUS, unite_vent="km/h",
                            zones_favorites="Tunis,Sousse", canaux_notification="EMAIL",
                            heure_debut_silence=time(22, 0), heure_fin_silence=time(7, 0), user=self.user)
        )
        print("Préférences créées :", preferences.id_preference)
        self.assertEqual("fr", repository.find_by_id(preferences.id_preference).langue)
        self.assertEqual(preferences.id_preference, User.objects.get(pk=self.user.id_user).preferences.id_preference)

    def test_health_profile_repository(self):
        repository = IHealthProfileRepository()
        profil = repository.save(
            HealthProfile(situation=SituationParticuliere.SPORTIF, score_sensibilite=40,
                          partage_autorise=True, user=self.user)
        )
        print("Profil santé créé :", profil.id_profile)
        self.assertEqual(40, repository.find_by_id(profil.id_profile).score_sensibilite)
        self.assertEqual(1, repository.count())

    def test_sensitivity_repository(self):
        profil = IHealthProfileRepository().save(HealthProfile(situation=SituationParticuliere.AUCUNE, user=self.user))
        repository = ISensitivityRepository()
        sensibilite = repository.save(
            Sensitivity(type=TypeSensibilite.ASTHME, niveau=NiveauSensibilite.MODERE,
                        facteurs_declencheurs="Pollution, froid", health_profile=profil)
        )
        print("Sensibilité créée :", sensibilite.id_sensitivity)
        self.assertEqual(TypeSensibilite.ASTHME, repository.find_by_id(sensibilite.id_sensitivity).type)
        self.assertEqual(1, profil.sensitivities.count())

    def test_api_key_repository(self):
        repository = IApiKeyRepository()
        cle = repository.save(
            ApiKey(cle="cle-test-123", date_creation=date(2026, 11, 26), date_expiration=date(2027, 11, 26),
                   user=self.user)
        )
        print("Clé API créée :", cle.id_api_key)
        self.assertTrue(repository.exists_by_id(cle.id_api_key))
        self.assertEqual(1, self.user.api_keys.count())

    def test_refresh_token_repository(self):
        repository = IRefreshTokenRepository()
        jeton = repository.save(
            RefreshToken(token="jeton-test", date_expiration=datetime(2026, 12, 26, tzinfo=timezone.utc),
                         revoque=False, user=self.user)
        )
        print("Jeton créé :", jeton.id_token)
        self.assertFalse(repository.find_by_id(jeton.id_token).revoque)
        repository.delete_by_id(jeton.id_token)
        self.assertIsNone(repository.find_by_id(jeton.id_token))
