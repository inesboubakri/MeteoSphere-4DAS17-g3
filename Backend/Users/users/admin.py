"""Tables visibles dans l'interface d'administration Django (http://localhost:8081/admin/)."""
from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from users.models import ApiKey, HealthProfile, RefreshToken, Role, RoleRequest, Sensitivity, User, UserPreferences


@admin.register(User)
class MeteoUserAdmin(UserAdmin):
    fieldsets = UserAdmin.fieldsets + (
        ("MeteoSphere", {"fields": ("role", "statut", "ville_residence", "date_inscription")}),
    )
    list_display = ("id_user", "username", "email", "role", "statut", "is_superuser")


for model in (Role, RoleRequest, UserPreferences, HealthProfile, Sensitivity, ApiKey, RefreshToken):
    admin.site.register(model)
