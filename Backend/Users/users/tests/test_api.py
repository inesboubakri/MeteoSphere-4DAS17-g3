"""Tests de l'API : authentification JWT, JSON camelCase et droits d'accès."""
import jwt
from django.conf import settings
from rest_framework.test import APITestCase

from users.enums import RoleName
from users.models import ApiKey, RefreshToken, Role, User, UserPreferences


class ApiTests(APITestCase):
    def setUp(self):
        self.admin_role = Role.objects.create(nom=RoleName.ADMIN)
        self.admin = User.objects.create_user(username="admin", email="admin@meteo.tn", password="admin12345",
                                              role=self.admin_role)

    def register(self, username):
        r = self.client.post("/api/auth/register", {"username": username, "email": f"{username}@meteo.tn",
                                                    "password": "motdepasse1", "villeResidence": "Tunis"},
                             format="json")
        self.assertEqual(201, r.status_code, r.content)
        return r.json()

    def login(self, username, password="motdepasse1"):
        r = self.client.post("/api/auth/login", {"username": username, "password": password}, format="json")
        self.assertEqual(200, r.status_code, r.content)
        return r.json()

    def as_user(self, tokens):
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {tokens['access']}")

    def test_register_returns_camel_case_without_password(self):
        body = self.register("sara")
        self.assertIn("idUser", body)
        self.assertIn("villeResidence", body)
        self.assertIn("dateInscription", body)
        self.assertNotIn("password", body)
        self.assertEqual(RoleName.CITOYEN, Role.objects.get(pk=body["role"]).nom)

    def test_jwt_contains_user_id_and_role(self):
        user = self.register("sara")
        tokens = self.login("sara")
        claims = jwt.decode(tokens["access"], settings.SIMPLE_JWT["SIGNING_KEY"], algorithms=["HS256"])
        self.assertEqual(user["idUser"], claims["userId"])
        self.assertEqual("CITOYEN", claims["role"])
        self.assertEqual("access", claims["token_type"])

    def test_wrong_password_and_missing_token(self):
        self.register("sara")
        r = self.client.post("/api/auth/login", {"username": "sara", "password": "faux"}, format="json")
        self.assertEqual(401, r.status_code)
        self.assertEqual(401, r.json()["statut"])
        self.assertEqual(401, self.client.get("/api/users").status_code)
        self.client.credentials(HTTP_AUTHORIZATION="Bearer jeton.bidon.ici")
        self.assertEqual(401, self.client.get("/api/auth/me").status_code)

    def test_refresh_then_logout_revokes(self):
        self.register("sara")
        tokens = self.login("sara")
        r = self.client.post("/api/auth/refresh", {"refresh": tokens["refresh"]}, format="json")
        self.assertEqual(200, r.status_code)
        self.assertIn("access", r.json())
        self.assertEqual(204, self.client.post("/api/auth/logout", {"refresh": tokens["refresh"]},
                                               format="json").status_code)
        r = self.client.post("/api/auth/refresh", {"refresh": tokens["refresh"]}, format="json")
        self.assertEqual(401, r.status_code)

    def test_user_sees_only_own_data(self):
        sara = self.register("sara")
        omar = self.register("omar")
        self.as_user(self.login("sara"))
        self.assertEqual(200, self.client.get(f"/api/users/{sara['idUser']}").status_code)
        self.assertEqual(403, self.client.get(f"/api/users/{omar['idUser']}").status_code)
        liste = self.client.get("/api/users").json()
        self.assertEqual([sara["idUser"]], [u["idUser"] for u in liste])
        self.assertEqual(403, self.client.delete(f"/api/users/{omar['idUser']}").status_code)

    def test_user_cannot_change_own_statut_or_manage_roles(self):
        sara = self.register("sara")
        self.as_user(self.login("sara"))
        r = self.client.put(f"/api/users/{sara['idUser']}", {"username": "sara", "email": "sara@meteo.tn",
                                                             "statut": "SUSPENDU", "villeResidence": "Sfax"},
                            format="json")
        self.assertEqual(200, r.status_code, r.content)
        self.assertEqual("ACTIF", r.json()["statut"])
        self.assertEqual("Sfax", r.json()["villeResidence"])
        self.assertEqual(403, self.client.post("/api/roles", {"nom": "ADMIN"}, format="json").status_code)
        self.assertEqual(200, self.client.get("/api/roles").status_code)

    def test_owned_resource_is_always_created_for_caller(self):
        sara = self.register("sara")
        omar = self.register("omar")
        self.as_user(self.login("sara"))
        r = self.client.post("/api/user-preferences", {"langue": "fr", "user": omar["idUser"]}, format="json")
        self.assertEqual(201, r.status_code, r.content)
        self.assertEqual(sara["idUser"], r.json()["user"])
        r = self.client.post("/api/user-preferences", {"langue": "en"}, format="json")
        self.assertEqual(409, r.status_code)

    def test_role_request_approved_by_admin_gives_role(self):
        sara = self.register("sara")
        self.as_user(self.login("sara"))
        r = self.client.post("/api/role-requests", {"roleDemande": "METEOROLOGUE", "justificatif": "Diplôme"},
                             format="json")
        self.assertEqual(201, r.status_code, r.content)
        demande = r.json()
        self.assertEqual("EN_ATTENTE", demande["statut"])
        self.assertEqual(403, self.client.put(f"/api/role-requests/{demande['idRequest']}",
                                              {"roleDemande": "METEOROLOGUE", "statut": "APPROUVEE"},
                                              format="json").status_code)
        self.as_user(self.login("admin", "admin12345"))
        r = self.client.put(f"/api/role-requests/{demande['idRequest']}",
                            {"roleDemande": "METEOROLOGUE", "statut": "APPROUVEE"}, format="json")
        self.assertEqual(200, r.status_code, r.content)
        self.assertEqual(RoleName.METEOROLOGUE, User.objects.get(pk=sara["idUser"]).role.nom)

    def test_admin_sees_everything(self):
        self.register("sara")
        self.register("omar")
        self.as_user(self.login("admin", "admin12345"))
        self.assertEqual(3, len(self.client.get("/api/users").json()))
        self.assertEqual(200, self.client.get("/api/refresh-tokens").status_code)

    def test_admin_deletes_user_with_tokens_keys_and_preferences(self):
        sara = self.register("sara")
        self.as_user(self.login("sara"))
        self.client.post("/api/user-preferences", {"langue": "fr"}, format="json")
        self.client.post("/api/api-keys", {"cle": "k-1"}, format="json")
        self.as_user(self.login("admin", "admin12345"))
        r = self.client.delete(f"/api/users/{sara['idUser']}")
        self.assertEqual(204, r.status_code, r.content)
        self.assertFalse(RefreshToken.objects.filter(user_id=sara["idUser"]).exists())
        self.assertFalse(ApiKey.objects.filter(user_id=sara["idUser"]).exists())
        self.assertFalse(UserPreferences.objects.filter(user_id=sara["idUser"]).exists())

    def test_swagger_is_public(self):
        self.assertEqual(200, self.client.get("/api/v3/api-docs").status_code)
