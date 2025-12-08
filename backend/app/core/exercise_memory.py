# backend/app/core/exercise_memory.py

from typing import Optional

class EphemeralMemory:
    """
    Memoria efímera para Modo B (ET v1.4, cap. 4.3.2).
    No persistente. Vive solo en el proceso del backend.
    Guarda únicamente el último lexical_item_id por categoría.
    """

    def __init__(self):
        # { category_id: lexical_item_id }
        self._last_items: dict[int, int] = {}

    def get_last(self, category_id: int) -> Optional[int]:
        return self._last_items.get(category_id)

    def set_last(self, category_id: int, lexical_item_id: int) -> None:
        self._last_items[category_id] = lexical_item_id


# Instancia global única permitida (memoria efímera del proceso)
ephemeral_memory = EphemeralMemory()
