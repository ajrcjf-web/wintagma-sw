from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from sqlalchemy.orm import Session
import logging

from app.core.db import SessionLocal
from app.models.category import Category
from app.core.exercise_service import ExerciseService

router = APIRouter()
logger = logging.getLogger(__name__)


# ----- Request Schema -----
class GenerateExerciseRequest(BaseModel):
    category_id: int
    previous_lexical_item_id: int | None = None


# ----- Response Schema -----
class ExerciseOptionResponse(BaseModel):
    option_id: int
    text: str


class GenerateExerciseResponse(BaseModel):
    exercise_id: int
    prompt: str
    options: list[ExerciseOptionResponse]


@router.post("/generate", response_model=GenerateExerciseResponse)
def generate_exercise(payload: GenerateExerciseRequest):
    db: Session = SessionLocal()

    try:
        # 1. Verificar categoría existente
        category = db.query(Category).filter(
            Category.category_id == payload.category_id
        ).first()

        if category is None:
            return JSONResponse(
                status_code=404,
                content={"error": "category_not_found"}
            )

        # 2. Generar ejercicio
        try:
            service = ExerciseService(db=db)   # <-- CORRECTO: instanciamos servicio
            exercise = service.generate_exercise(
                category_id=payload.category_id,
                previous_lexical_item_id=payload.previous_lexical_item_id,
            )

        except ValueError as e:
            if str(e) == "insufficient_items":
                return JSONResponse(
                    status_code=400,
                    content={"error": "insufficient_items"}
                )
            logger.exception("Unhandled ValueError in ExerciseService")
            return JSONResponse(
                status_code=500,
                content={"error": "internal_error"}
            )

        except Exception as e:
            logger.exception("Unhandled exception in ExerciseService")
            return JSONResponse(
                status_code=500,
                content={"error": "internal_error"}
            )

        # 3. Persistir Exercise y Options
        try:
            db.add(exercise)
            db.commit()
            db.refresh(exercise)
        except Exception:
            logger.exception("Error persisting exercise")
            db.rollback()
            raise HTTPException(status_code=500, detail="internal_error")

        # 4. Construir respuesta normativa segura
        try:
            correct_options = [o for o in exercise.options if o.is_correct]
            if not correct_options:
                raise RuntimeError("No correct option found")

            prompt_text = correct_options[0].text

            response = GenerateExerciseResponse(
                exercise_id=exercise.exercise_id,
                prompt=prompt_text,
                options=[
                    ExerciseOptionResponse(
                        option_id=opt.option_id,
                        text=opt.text
                    )
                    for opt in exercise.options
                ],
            )

            return response

        except Exception:
            logger.exception("Error building response for exercise")
            raise HTTPException(status_code=500, detail="internal_error")

    except Exception:
        logger.exception("Unhandled exception in /exercise/generate root")
        return JSONResponse(
            status_code=500,
            content={"error": "internal_error"}
        )

    finally:
        db.close()
