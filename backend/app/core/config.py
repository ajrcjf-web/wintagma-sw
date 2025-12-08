# backend/app/core/config.py
import os
from pydantic import BaseModel


class Settings(BaseModel):
    database_url: str


def get_settings() -> Settings:
    """
    Lee la configuración mínima desde variables de entorno.

    - DATABASE_URL: URL completa de conexión a PostgreSQL 15.
    """
    database_url = os.getenv(
        "DATABASE_URL",
        "postgresql+psycopg://wintagma:wintagma@localhost:5432/wintagma_dev",
    )
    return Settings(database_url=database_url)
