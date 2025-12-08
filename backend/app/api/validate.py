from fastapi import APIRouter
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.core.db import SessionLocal
from app.models.exercise import Exercise
from app.models.exercise_option import ExerciseOption

router = APIRouter()


# ----- Request Schema -----
class ValidateExerciseRequest(BaseModel):
    exercise_id: int
    selected_option_id: int


# ----- Response Schema -----
class ValidateExerciseResponse(BaseModel):
    correct: bool
    correct_option_id: int
    score_delta: int


@router.post("/validate", response_model=ValidateExerciseResponse)
def validate_exercise(payload: ValidateExerciseRequest):
    db: Session = SessionLocal()

    try:
        # Recuperar ejercicio
        exercise = (
            db.query(Exercise)
            .filter(Exercise.exercise_id == payload.exercise_id)
            .first()
        )

        if exercise is None:
            return JSONResponse(
                status_code=404,
                content={"error": "exercise_not_found"}
            )

        # Recuperar opciones asociadas
        options = (
            db.query(ExerciseOption)
            .filter(ExerciseOption.exercise_id == payload.exercise_id)
            .all()
        )

        option_ids = [opt.option_id for opt in options]
        correct_option = next((opt for opt in options if opt.is_correct), None)

        # Validación básica normativa
        if payload.selected_option_id not in option_ids:
            return JSONResponse(
                status_code=400,
                content={"error": "invalid_option_id"}
            )

        if correct_option is None:
            return JSONResponse(
                status_code=500,
                content={"error": "internal_error"}
            )

        # Regla estrícta ET v1.4 — determinista
        correct = (payload.selected_option_id == correct_option.option_id)
        score_delta = 1 if correct else 0

        return ValidateExerciseResponse(
            correct=correct,
            correct_option_id=correct_option.option_id,
            score_delta=score_delta,
        )

    except Exception:
        return JSONResponse(
            status_code=500,
            content={"error": "internal_error"}
        )

    finally:
        db.close()
