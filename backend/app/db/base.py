# backend/app/db/base.py
from sqlalchemy.orm import DeclarativeBase


class Base(DeclarativeBase):
    """
    Base declarativa para todos los modelos ORM del proyecto.

    Los modelos Category, LexicalItem, Exercise y ExerciseOption
    se definirán en MPs de tipo DATA, importando esta Base.
    """
    pass
