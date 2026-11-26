import os
import sys

from django.apps import AppConfig


class UsersConfig(AppConfig):
    name = "users"
    verbose_name = "Utilisateurs & Santé"

    def ready(self):
        # Inscription auprès d'Eureka uniquement quand le serveur web démarre
        # (pas pendant migrate, test, createsuperuser...).
        # RUN_MAIN vaut "true" dans le processus relancé par l'autoreload de runserver.
        if "runserver" in sys.argv and os.environ.get("RUN_MAIN") == "true":
            from users.eureka import register_with_eureka

            register_with_eureka()
