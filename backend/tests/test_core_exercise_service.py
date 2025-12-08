# backend/tests/test_core_exercise_service.py

from app.core.exercise_service import ExerciseService
from app.core.exercise_memory import ephemeral_memory
from app.models.lexical_item import LexicalItem


class FakeQuery:
    """
    Query mínima para ExerciseService:

    - session.query(LexicalItem) → FakeQuery con una lista fija de items
    - .filter(...) → se ignora la condición y se devuelve self
    - .all() → devuelve la lista de items

    Esto es suficiente para la lógica de ExerciseService que solo hace:
        session.query(LexicalItem).filter(...).all()
    """

    def __init__(self, items):
        self._items = items

    def filter(self, *_args, **_kwargs):
        return self

    def all(self):
        # Devolvemos copia por seguridad
        return list(self._items)


class FakeSession:
    """
    Sesión falsa de SQLAlchemy.

    Devuelve una FakeQuery preconfigurada cuando se hace:
        session.query(LexicalItem)
    No toca ninguna base de datos real.
    """

    def __init__(self, items):
        self._items = items

    def query(self, model):
        if model is LexicalItem:
            return FakeQuery(self._items)
        # Si por algún motivo se consulta otro modelo, devolvemos lista vacía
        return FakeQuery([])


def _make_items_for_category(category_id: int, count: int = 5) -> list[LexicalItem]:
    """
    Crea 'count' LexicalItem en memoria para una categoría dada.
    No hay BD real, solo objetos ORM en memoria.
    """
    items: list[LexicalItem] = []
    for i in range(1, count + 1):
        items.append(
            LexicalItem(
                lexical_item_id=i,
                category_id=category_id,
                text=f"wort{i}",
            )
        )
    return items


def test_generate_simple():
    """
    Generar un ejercicio simple:

    - category_id = 1
    - 5 opciones
    - exactamente 1 correcta
    """
    # Aseguramos memoria efímera limpia
    if hasattr(ephemeral_memory, "_last_items"):
        ephemeral_memory._last_items.clear()

    items = _make_items_for_category(category_id=1, count=5)
    db = FakeSession(items)

    # Forma oficial (instancia)
    service = ExerciseService(db)

    exercise = service.generate_exercise(
        category_id=1,
        previous_lexical_item_id=None,
    )

    assert exercise.category_id == 1
    assert len(exercise.options) == 5
    assert sum(1 for opt in exercise.options if opt.is_correct) == 1


def test_generate_no_immediate_repetition_modo_b():
    """
    Verifica la regla de no repetición inmediata (Modo B)
    usando la memoria efímera:

    Dos ejercicios consecutivos en la misma categoría
    no deben usar el mismo lexical_item_id cuando hay >1 ítem.
    """
    if hasattr(ephemeral_memory, "_last_items"):
        ephemeral_memory._last_items.clear()

    items = _make_items_for_category(category_id=1, count=5)
    db = FakeSession(items)
    service = ExerciseService(db)

    first = service.generate_exercise(
        category_id=1,
        previous_lexical_item_id=None,
    )
    first_lex_id = first.lexical_item_id

    second = service.generate_exercise(
        category_id=1,
        previous_lexical_item_id=first_lex_id,
    )

    # Con más de un ítem, el segundo NO debe repetir el lexical_item anterior
    assert second.lexical_item_id != first_lex_id
