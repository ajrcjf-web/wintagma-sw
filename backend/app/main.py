from fastapi import FastAPI

# Routers del backend
from app.api import content
from app.api import exercise as exercise_api
from app.api import validate as validate_api


def create_app() -> FastAPI:
    app = FastAPI(title="Wintagma SW Backend")

    # ----- Routers del módulo CONTENT -----
    app.include_router(
        content.router,
        prefix="/content",
        tags=["content"]
    )

    # ----- Routers del módulo EXERCISE -----
    app.include_router(
        exercise_api.router,
        prefix="/exercise",
        tags=["exercise"]
    )

    # ----- Routers del módulo VALIDATION -----
    app.include_router(
        validate_api.router,
        prefix="/exercise",
        tags=["exercise"]
    )

    return app


# Instancia pública de la aplicación FastAPI
app = create_app()
