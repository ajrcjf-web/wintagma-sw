# backend/app/core/exercise_service.py

import random
from typing import Optional

from app.core.exercise_memory import ephemeral_memory
from app.models.lexical_item import LexicalItem
from app.models.exercise import Exercise
from app.models.exercise_option import ExerciseOption


class ExerciseService:
    """
    Servicio de generación de ejercicios conforme a ET v1.4:

    - Selección aleatoria dentro de la categoría.
    - No repetición inmediata (Modo B) usando memoria efímera.
    - 1 opción correcta + 4 distractores.
    - Aleatorización del orden de opciones.

    Además, esta implementación acepta de forma flexible varios patrones de llamada
    para evitar TypeError por 'db' como keyword en tests antiguos, sin cambiar
    la lógica de negocio.
    """

    def __init__(self, db):
        # Sesión de base de datos (SQLAlchemy Session)
        self.db = db

    def _generate_core(
        self,
        session,
        category_id: int,
        previous_lexical_item_id: Optional[int],
    ) -> Exercise:
        """
        Lógica central de generación de ejercicio.
        Separamos esto en un método privado para poder reutilizarlo
        desde el wrapper flexible generate_exercise().
        """
        # 1. Obtener todos los items de la categoría
        items = (
            session.query(LexicalItem)
            .filter(LexicalItem.category_id == category_id)
            .all()
        )

        if not items:
            # La ET habla de insufficient_items a nivel de API; aquí usamos
            # ValueError para que la capa API lo traduzca al error normativo.
            raise ValueError("insufficient_items")

        # 2. NO REPETICIÓN INMEDIATA — MODO B (memoria efímera, no persistente)
        last_mem = ephemeral_memory.get_last(category_id)

        candidates = items
        if len(items) > 1 and last_mem is not None:
            # Excluimos el último lexical_item si hay más de uno
            filtered = [
                i for i in items if i.lexical_item_id != last_mem
            ]
            candidates = filtered or items  # fallback por seguridad

        # Selección aleatoria del lexical item correcto
        correct_item = random.choice(candidates)

        # Actualizar memoria efímera
        ephemeral_memory.set_last(category_id, correct_item.lexical_item_id)

        # 3. DISTRACTORES (misma categoría, 4 elementos)
        distractor_pool = [
            i for i in items if i.lexical_item_id != correct_item.lexical_item_id
        ]

        # Asumimos que hay al menos 4 distractores disponibles según el baseline de datos
        distractors = random.sample(distractor_pool, 4)

        # 4. Construcción de opciones y aleatorización
        option_texts = [correct_item.text] + [d.text for d in distractors]
        random.shuffle(option_texts)

        option_order = [1, 2, 3, 4, 5]

        exercise = Exercise(
            category_id=category_id,
            lexical_item_id=correct_item.lexical_item_id,
            option_order=option_order,
        )

        exercise.options = []
        for idx, text in enumerate(option_texts, start=1):
            exercise.options.append(
                ExerciseOption(
                    option_id=idx,
                    text=text,
                    is_correct=(text == correct_item.text),
                )
            )

        return exercise

    def generate_exercise(*args, **kwargs) -> Exercise:
        """
        Wrapper flexible que permite TODAS estas formas sin lanzar TypeError:

        1) Estilo instancia (oficial):
            service = ExerciseService(db)
            service.generate_exercise(category_id=1, previous_lexical_item_id=None)

        2) Estilo antiguo de algunos tests:
            ExerciseService.generate_exercise(db=db, category_id=1)
            ExerciseService.generate_exercise(db=db, category_id=1,
                                              previous_lexical_item_id=123)

        Acepta 'db' como keyword SIN romper, aunque no esté en la firma original.
        """

        # ---- Desempaquetar self / cls y argumentos ----
        if not args:
            # Llamada totalmente estática sin self, no la soportamos
            raise ValueError("generate_exercise must be called on class or instance")

        self_or_cls = args[0]
        remaining_args = list(args[1:])

        # Extraer kwargs conocidos
        db = kwargs.pop("db", None)
        category_id = kwargs.pop("category_id", None)
        previous_lexical_item_id = kwargs.pop("previous_lexical_item_id", None)

        # Rellenar desde args posicionales si hace falta
        if category_id is None and remaining_args:
            category_id = remaining_args.pop(0)

        if previous_lexical_item_id is None and remaining_args:
            previous_lexical_item_id = remaining_args.pop(0)

        # Determinar la sesión
        session = db
        # Si no nos pasan db explícito, intentamos usar el atributo .db de la instancia
        if session is None and hasattr(self_or_cls, "db"):
            session = getattr(self_or_cls, "db")

        if session is None:
            raise ValueError("No database session provided to ExerciseService")

        if category_id is None:
            raise ValueError("category_id is required for generate_exercise")

        # Si self_or_cls es una instancia de ExerciseService, usamos su _generate_core.
        # Si es la propia clase, instanciamos un servicio temporal.
        if isinstance(self_or_cls, ExerciseService):
            service = self_or_cls
        else:
            # Llamada vía clase: ExerciseService.generate_exercise(...)
            service = ExerciseService(session)

        return service._generate_core(
            session=session,
            category_id=category_id,
            previous_lexical_item_id=previous_lexical_item_id,
        )
