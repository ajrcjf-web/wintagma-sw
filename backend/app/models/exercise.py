from typing import List

from sqlalchemy import Integer, ForeignKey
from sqlalchemy.dialects.postgresql import ARRAY
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class Exercise(Base):
    __tablename__ = "exercise"

    exercise_id: Mapped[int] = mapped_column(
        Integer, primary_key=True, autoincrement=True
    )
    category_id: Mapped[int] = mapped_column(
        ForeignKey("category.category_id"), nullable=False
    )
    lexical_item_id: Mapped[int] = mapped_column(
        ForeignKey("lexical_item.lexical_item_id"), nullable=False
    )
    # option_order: [1..5] según ET v1.4
    option_order: Mapped[List[int]] = mapped_column(
        ARRAY(Integer), nullable=False
    )

    # Relaciones mínimas, sin alterar ingeniería
    category = relationship("Category")
    lexical_item = relationship("LexicalItem")
    options = relationship(
        "ExerciseOption",
        back_populates="exercise",
        cascade="all, delete-orphan",
    )
