"""Configuration du microservice Utilisateurs & Santé (équivalent de application.properties)."""
import os
from datetime import timedelta
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent

# Service
APPLICATION_NAME = "users"
SERVER_PORT = 8081

# Clé de développement ; en production elle vient de la variable DJANGO_SECRET_KEY
SECRET_KEY = os.environ.get("DJANGO_SECRET_KEY", "django-insecure-dev-only-change-me")
DEBUG = os.environ.get("DJANGO_DEBUG", "true").lower() == "true"
ALLOWED_HOSTS = ["localhost", "127.0.0.1", "[::1]"]

# Eureka registration
EUREKA_CLIENT_ENABLED = os.environ.get("EUREKA_CLIENT_ENABLED", "true").lower() == "true"
EUREKA_SERVER_URL = os.environ.get("EUREKA_SERVER_URL", "http://localhost:8761/eureka")

INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    "rest_framework",
    "drf_spectacular",
    "users",
]

MIDDLEWARE = [
    "django.middleware.security.SecurityMiddleware",
    "django.contrib.sessions.middleware.SessionMiddleware",
    "django.middleware.common.CommonMiddleware",
    "django.middleware.csrf.CsrfViewMiddleware",
    "django.contrib.auth.middleware.AuthenticationMiddleware",
    "django.contrib.messages.middleware.MessageMiddleware",
    "django.middleware.clickjacking.XFrameOptionsMiddleware",
]

ROOT_URLCONF = "users_service.urls"

TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [],
        "APP_DIRS": True,
        "OPTIONS": {
            "context_processors": [
                "django.template.context_processors.request",
                "django.contrib.auth.context_processors.auth",
                "django.contrib.messages.context_processors.messages",
            ],
        },
    },
]

WSGI_APPLICATION = "users_service.wsgi.application"

# Connexion à la base de données (SQLite : base embarquée, fichier local)
DATABASES = {
    "default": {
        "ENGINE": "django.db.backends.sqlite3",
        "NAME": BASE_DIR / "users_db.sqlite3",
    }
}

DEFAULT_AUTO_FIELD = "django.db.models.BigAutoField"

AUTH_PASSWORD_VALIDATORS = [
    {"NAME": "django.contrib.auth.password_validation.UserAttributeSimilarityValidator"},
    {"NAME": "django.contrib.auth.password_validation.MinimumLengthValidator"},
]

LANGUAGE_CODE = "fr-fr"
TIME_ZONE = "Africa/Tunis"
USE_I18N = True
USE_TZ = True

STATIC_URL = "static/"

# Le modèle users.User remplace l'utilisateur intégré de Django (table "users")
AUTH_USER_MODEL = "users.User"

# API REST + Swagger
REST_FRAMEWORK = {
    "DEFAULT_SCHEMA_CLASS": "drf_spectacular.openapi.AutoSchema",
    "EXCEPTION_HANDLER": "users.exceptions.global_exception_handler",
    # Toute requête doit porter un JWT valide, sauf les routes /api/auth/ déclarées publiques
    "DEFAULT_AUTHENTICATION_CLASSES": ["rest_framework_simplejwt.authentication.JWTAuthentication"],
    "DEFAULT_PERMISSION_CLASSES": ["rest_framework.permissions.IsAuthenticated"],
    # JSON en camelCase (idUser, dateInscription) comme les services Spring ; le code Python reste en snake_case
    "DEFAULT_RENDERER_CLASSES": ["djangorestframework_camel_case.render.CamelCaseJSONRenderer"],
    "DEFAULT_PARSER_CLASSES": ["djangorestframework_camel_case.parser.CamelCaseJSONParser"],
}

# JWT partagé avec la Gateway et les services Spring.
# HS256 + clé secrète commune : chaque service peut vérifier le jeton sans appeler Django.
# Contenu du jeton : userId, username, role.
SIMPLE_JWT = {
    "ALGORITHM": "HS256",
    "SIGNING_KEY": os.environ.get("JWT_SECRET", "dev-only-jwt-secret-change-me-32-bytes-minimum"),
    "ACCESS_TOKEN_LIFETIME": timedelta(minutes=int(os.environ.get("JWT_ACCESS_MINUTES", "15"))),
    "REFRESH_TOKEN_LIFETIME": timedelta(days=int(os.environ.get("JWT_REFRESH_DAYS", "7"))),
    "USER_ID_FIELD": "id_user",
    "USER_ID_CLAIM": "userId",
    "AUTH_HEADER_TYPES": ("Bearer",),
    "UPDATE_LAST_LOGIN": True,
}

SPECTACULAR_SETTINGS = {
    "TITLE": "Utilisateurs & Santé API",
    "DESCRIPTION": "API REST d'authentification (JWT) et de gestion des utilisateurs, rôles, préférences et profils santé",
    "VERSION": "1.0.0",
    "SERVE_INCLUDE_SCHEMA": False,
    # la documentation reste consultable sans jeton ; le bouton "Authorize" sert à tester les routes protégées
    "SERVE_PERMISSIONS": ["rest_framework.permissions.AllowAny"],
    "SERVE_AUTHENTICATION": [],
    "ENUM_NAME_OVERRIDES": {"RoleNameEnum": "users.enums.RoleName"},
    "POSTPROCESSING_HOOKS": [
        "drf_spectacular.hooks.postprocess_schema_enums",
        "drf_spectacular.contrib.djangorestframework_camel_case.camelize_serializer_fields",
    ],
    # Swagger UI garde le jeton saisi dans "Authorize" entre deux rechargements
    "SWAGGER_UI_SETTINGS": {"persistAuthorization": True},
}

# Logs (équivalent de show-sql / logging.level)
LOGGING = {
    "version": 1,
    "disable_existing_loggers": False,
    "handlers": {"console": {"class": "logging.StreamHandler"}},
    "loggers": {
        "users": {"handlers": ["console"], "level": "INFO"},
        "django.db.backends": {
            "handlers": ["console"],
            "level": os.environ.get("SQL_LOG_LEVEL", "INFO"),
        },
    },
}
