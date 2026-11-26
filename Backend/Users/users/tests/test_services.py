"""Atelier 4 - Partie 2 : tests des services (un test par entité)."""
from datetime import date, datetime, timezone

from django.contrib.auth.hashers import check_password
from django.test import TestCase

from users.enums import NiveauSensibilite, RoleName, SituationParticuliere, StatutCompte, StatutDemande, TypeSensibilite
from users.exceptions import ResourceNotFoundException
from users.models import ApiKey, HealthProfile, RefreshToken, Role, RoleRequest, Sensitivity, User, UserPreferences
from users.services import (
    ApiKeyServiceImpl,
    HealthProfileServiceImpl,
    RefreshTokenServiceImpl,
    RoleRequestServiceImpl,
    RoleServiceImpl,
    SensitivityServiceImpl,
    UserPreferencesServiceImpl,
    UserServiceImpl,
)


class ServiceTests(TestCase):
    def setUp(self):
        self.role_service = RoleServiceImpl()
        self.user_service = UserServiceImpl()
        self.role = self.role_service.create(Role(nom=RoleName.CITOYEN))
        self.user = self.user_service.create(
            User(username="ines", email="ines@meteosphere.tn", password="secret123",
                 statut=StatutCompte.ACTIF, date_inscription=date(2026, 11, 26), role=self.role)
        )

    def test_role_service(self):
        service = self.role_service
        admin = service.create(Role(nom=RoleName.ADMIN))
        self.assertEqual(2, len(service.find_all()))
        modifie = service.update(admin.id_role, Role(nom=RoleName.METEOROLOGUE))
        self.assertEqual(RoleName.METEOROLOGUE, modifie.nom)
        with self.assertRaises(ValueError):
            service.create(Role(nom=None))
        service.delete_by_id(admin.id_role)
        with self.assertRaises(ResourceNotFoundException):
            service.find_by_id(admin.id_role)

    def test_user_service(self):
        service = self.user_service
        trouve = service.find_by_id(self.user.id_user)
        print("Mot de passe stocké :", trouve.password[:20], "...")
        self.assertNotEqual("secret123", trouve.password)
        self.assertTrue(check_password("secret123", trouve.password))
        with self.assertRaises(ValueError):
            service.create(User(username="sans-email", email="pas-un-email"))
        with self.assertRaises(ValueError):
            service.create(User(id_user=99, username="x", email="x@x.tn"))
        modifie = service.update(self.user.id_user, User(username="ines.b", email="ines@meteosphere.tn",
                                                          statut=StatutCompte.SUSPENDU))
        self.assertEqual(StatutCompte.SUSPENDU, modifie.statut)
        self.assertEqual(self.role.id_role, modifie.role.id_role)

    def test_role_request_service(self):
        service = RoleRequestServiceImpl()
        demande = service.create(RoleRequest(role_demande=RoleName.METEOROLOGUE, statut=StatutDemande.EN_ATTENTE,
                                             date_demande=datetime(2026, 11, 26, tzinfo=timezone.utc), user=self.user))
        modifie = service.update(demande.id_request, RoleRequest(role_demande=RoleName.METEOROLOGUE,
                                                                 statut=StatutDemande.APPROUVEE))
        self.assertEqual(StatutDemande.APPROUVEE, modifie.statut)
        with self.assertRaises(ValueError):
            service.create(RoleRequest(role_demande=None, user=self.user))
        service.delete_by_id(demande.id_request)
        with self.assertRaises(ResourceNotFoundException):
            service.delete_by_id(demande.id_request)

    def test_user_preferences_service(self):
        service = UserPreferencesServiceImpl()
        preferences = service.create(UserPreferences(langue="fr", unite_vent="km/h", user=self.user))
        modifie = service.update(preferences.id_preference, UserPreferences(langue="en", unite_vent="m/s"))
        self.assertEqual("en", modifie.langue)
        with self.assertRaises(ValueError):
            service.create(UserPreferences(langue=" "))
        self.assertEqual(1, len(service.find_all()))

    def test_health_profile_service(self):
        service = HealthProfileServiceImpl()
        profil = service.create(HealthProfile(situation=SituationParticuliere.ENFANT, score_sensibilite=70,
                                              user=self.user))
        self.assertEqual(70, service.find_by_id(profil.id_profile).score_sensibilite)
        with self.assertRaises(ValueError):
            service.update(profil.id_profile, HealthProfile(score_sensibilite=150))
        with self.assertRaises(ResourceNotFoundException):
            service.find_by_id(999)

    def test_sensitivity_service(self):
        profil = HealthProfileServiceImpl().create(HealthProfile(situation=SituationParticuliere.AUCUNE, user=self.user))
        service = SensitivityServiceImpl()
        sensibilite = service.create(Sensitivity(type=TypeSensibilite.UV, niveau=NiveauSensibilite.LEGER,
                                                 health_profile=profil))
        modifie = service.update(sensibilite.id_sensitivity, Sensitivity(type=TypeSensibilite.UV,
                                                                         niveau=NiveauSensibilite.SEVERE))
        self.assertEqual(NiveauSensibilite.SEVERE, modifie.niveau)
        with self.assertRaises(ValueError):
            service.create(Sensitivity(type=None))
        service.delete_by_id(sensibilite.id_sensitivity)
        self.assertEqual(0, len(service.find_all()))

    def test_api_key_service(self):
        service = ApiKeyServiceImpl()
        cle = service.create(ApiKey(cle="cle-123", date_creation=date(2026, 11, 26),
                                    date_expiration=date(2027, 11, 26), user=self.user))
        self.assertEqual("cle-123", service.find_by_id(cle.id_api_key).cle)
        with self.assertRaises(ValueError):
            service.create(ApiKey(cle="cle-456", date_creation=date(2026, 11, 26), date_expiration=date(2026, 1, 1)))
        service.delete_by_id(cle.id_api_key)
        with self.assertRaises(ResourceNotFoundException):
            service.find_by_id(cle.id_api_key)

    def test_refresh_token_service(self):
        service = RefreshTokenServiceImpl()
        jeton = service.create(RefreshToken(token="jeton-1", revoque=False, user=self.user,
                                            date_expiration=datetime(2026, 12, 26, tzinfo=timezone.utc)))
        modifie = service.update(jeton.id_token, RefreshToken(token="jeton-1", revoque=True))
        self.assertTrue(modifie.revoque)
        with self.assertRaises(ValueError):
            service.create(RefreshToken(token=""))
