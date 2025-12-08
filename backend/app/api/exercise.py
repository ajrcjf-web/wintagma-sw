from fastapi import APIRouter
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.core.db import SessionLocal
from app.models.category import Category
from app.core.exercise_service import ExerciseService

router = APIRouter()


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

        # 2. Generar ejercicio con servicio CORE-03
        try:
            exercise = ExerciseService.generate_exercise(
                db=db,
                category_id=payload.category_id,
                previous_lexical_item_id=payload.previous_lexical_item_id,
            )
        except ValueError as e:
            if str(e) == "insufficient_items":
                return JSONResponse(
                    status_code=400,
                    content={"error": "insufficient_items"}
                )
            return JSONResponse(
                status_code=500,
                content={"error": "internal_error"}
            )

        # 3. Persistir Exercise y ExerciseOptions
        db.add(exercise)
        db.commit()
        db.refresh(exercise)

        # 4. Construir respuesta normativa
        response = GenerateExerciseResponse(
            exercise_id=exercise.exercise_id,
            prompt=exercise.options[
                next(i for i, o in enumerate(exercise.options) if o.is_correct)
            ].text,
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
        return JSONResponse(
            status_code=500,
            content={"error": "internal_error"}
        )

    finally:
        db.close()
