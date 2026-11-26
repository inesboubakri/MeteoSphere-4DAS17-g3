#!/usr/bin/env python
"""Point d'entrée Django (équivalent de UsersApplication.main en Spring Boot)."""
import os
import sys


def main():
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "users_service.settings")
    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)


if __name__ == "__main__":
    main()
