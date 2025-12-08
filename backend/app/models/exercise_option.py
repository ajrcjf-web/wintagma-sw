from sqlalchemy import Integer, String, Boolean, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class ExerciseOption(Base):
    __tablename__ = "exercise_option"

    # Clave primaria compuesta: (exercise_id, option_id)
    exercise_id: Mapped[int] = mapped_column(
        ForeignKey("exercise.exercise_id"),
        primary_key=True,
    )
    option_id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
    )
    text: Mapped[str] = mapped_column(
        String,
        nullable=False,
    )
    is_correct: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
    )

    exercise = relationship(
        "Exercise",
        back_populates="options",
    )
