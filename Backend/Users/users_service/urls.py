"""Le préfixe /api est défini une seule fois ici (équivalent de server.servlet.context-path=/api)."""
from django.contrib import admin
from django.urls import include, path
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

urlpatterns = [
    path("admin/", admin.site.urls),
    path("api/", include("users.urls")),
    path("api/v3/api-docs", SpectacularAPIView.as_view(), name="api-docs"),
    path("api/swagger-ui.html", SpectacularSwaggerView.as_view(url_name="api-docs"), name="swagger-ui"),
]
