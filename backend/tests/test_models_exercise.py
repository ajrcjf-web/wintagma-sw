import pytest
from sqlalchemy.inspection import inspect as sa_inspect
from sqlalchemy.dialects.postgresql import ARRAY
from sqlalchemy.sql.sqltypes import String as SAString, Boolean as SABoolean

from app.models.exercise import Exercise
from app.models.exercise_option import ExerciseOption


def test_exercise_has_expected_columns():
    """
    Exercise debe tener exactamente:
      - exercise_id
      - category_id
      - lexical_item_id
      - option_order
    según ET v1.4 (cap. 3.1) y MP-DATA-02.
    """
    mapper = sa_inspect(Exercise)
    column_keys = {column.key for column in mapper.columns}

    assert column_keys == {
        "exercise_id",
        "category_id",
        "lexical_item_id",
        "option_order",
    }


def test_exercise_primary_key():
    """
    exercise_id es la clave primaria única de Exercise.
    """
    mapper = sa_inspect(Exercise)
    pk_keys = {column.key for column in mapper.primary_key}

    assert pk_keys == {"exercise_id"}


def test_exercise_has_foreign_keys_to_category_and_lexical_item():
    """
    Exercise.category_id y Exercise.lexical_item_id deben ser foreign keys
    hacia Category y LexicalItem respectivamente, sin fijar nombres de tabla.
    """
    mapper = sa_inspect(Exercise)

    category_id_col = mapper.columns["category_id"]
    lexical_item_id_col = mapper.columns["lexical_item_id"]

    assert len(list(category_id_col.foreign_keys)) == 1
    assert len(list(lexical_item_id_col.foreign_keys)) == 1


def test_exercise_option_has_expected_columns():
    """
    ExerciseOption debe tener exactamente:
      - exercise_id
      - option_id
      - text
      - is_correct
    según ET v1.4 y MP-DATA-02.
    """
    mapper = sa_inspect(ExerciseOption)
    column_keys = {column.key for column in mapper.columns}

    assert column_keys == {
        "exercise_id",
        "option_id",
        "text",
        "is_correct",
    }


def test_exercise_option_composite_primary_key():
    """
    ExerciseOption utiliza clave primaria compuesta (exercise_id, option_id),
    tal como define MP-DATA-02.
    """
    mapper = sa_inspect(ExerciseOption)
    pk_keys = {column.key for column in mapper.primary_key}

    assert pk_keys == {"exercise_id", "option_id"}


def test_exercise_option_relationships_exist():
    """
    Debe existir la relación Exercise.options ↔ ExerciseOption.exercise
    según MP-DATA-02.
    """
    assert hasattr(Exercise, "options"), "Exercise debe exponer .options"
    assert hasattr(ExerciseOption, "exercise"), "ExerciseOption debe exponer .exercise"


def test_option_order_uses_array_type():
    """
    option_order debe representarse como ARRAY(Integer) de PostgreSQL,
    según la decisión de implementación de MP-DATA-02.
    Aquí comprobamos que el tipo base es ARRAY.
    """
    mapper = sa_inspect(Exercise)
    option_order_col = mapper.columns["option_order"]

    assert isinstance(
        option_order_col.type, ARRAY
    ), "option_order debe usar tipo ARRAY(PostgreSQL)"


def test_exercise_option_text_and_is_correct_types():
    """
    Verifica que:
      - text se almacena como tipo de texto (String)
      - is_correct se almacena como Boolean
    Sin forzar longitudes ni constraints extra.
    """
    mapper = sa_inspect(ExerciseOption)
    text_col = mapper.columns["text"]
    is_correct_col = mapper.columns["is_correct"]

    assert isinstance(text_col.type, SAString)
    assert isinstance(is_correct_col.type, SABoolean)


def test_can_instantiate_exercise_and_options_in_memory():
    """
    Comprobación de que Exercise y ExerciseOption se pueden instanciar
    y relacionar en memoria sin base de datos.
    No valida reglas de negocio (distractores, etc.), solo estructura.
    """
    exercise = Exercise(
        category_id=1,
        lexical_item_id=1,
        option_order=[1, 2, 3, 4, 5],
    )

    option = ExerciseOption(
        exercise=exercise,
        option_id=1,
        text="Beispieloption",
        is_correct=True,
    )

    assert option.exercise is exercise
    assert option.option_id == 1
    assert option.text == "Beispieloption"
    assert option.is_correct is True
