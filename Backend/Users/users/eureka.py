"""Inscription du service auprès du serveur Eureka (équivalent du starter eureka-client de Spring)."""
import logging

from django.conf import settings

logger = logging.getLogger(__name__)


def register_with_eureka():
    if not settings.EUREKA_CLIENT_ENABLED:
        logger.info("Eureka désactivé (EUREKA_CLIENT_ENABLED=false)")
        return

    import py_eureka_client.eureka_client as eureka_client

    try:
        eureka_client.init(
            eureka_server=settings.EUREKA_SERVER_URL,
            app_name=settings.APPLICATION_NAME,
            instance_port=settings.SERVER_PORT,
            instance_host="localhost",
        )
        logger.info("Service '%s' inscrit sur Eureka (%s)", settings.APPLICATION_NAME, settings.EUREKA_SERVER_URL)
    except Exception as ex:  # le service doit démarrer même si Eureka est arrêté
        logger.warning("Inscription Eureka impossible : %s", ex)
