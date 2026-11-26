"""Routes du service (préfixées par /api dans users_service/urls.py)."""
from django.urls import path

from users import views

ROUTES = {
    "roles": views.RoleController,
    "users": views.UserController,
    "role-requests": views.RoleRequestController,
    "user-preferences": views.UserPreferencesController,
    "health-profiles": views.HealthProfileController,
    "sensitivities": views.SensitivityController,
    "api-keys": views.ApiKeyController,
    "refresh-tokens": views.RefreshTokenController,
}

urlpatterns = [
    path("auth/register", views.RegisterView.as_view(), name="auth-register"),
    path("auth/login", views.LoginView.as_view(), name="auth-login"),
    path("auth/refresh", views.RefreshView.as_view(), name="auth-refresh"),
    path("auth/logout", views.LogoutView.as_view(), name="auth-logout"),
    path("auth/me", views.MeView.as_view(), name="auth-me"),
]
for route, (collection, detail) in ROUTES.items():
    urlpatterns += [
        path(route, collection.as_view(), name=f"{route}-list"),
        path(f"{route}/<int:id>", detail.as_view(), name=f"{route}-detail"),
    ]
