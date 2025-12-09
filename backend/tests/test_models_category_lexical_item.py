from sqlalchemy.inspection import inspect as sa_inspect

from app.models.category import Category
from app.models.lexical_item import LexicalItem


def test_category_has_expected_columns():
    """
    Category debe tener exactamente:
      - category_id
      - name
    según el modelo lingüístico operativo de la ET v1.4 (cap. 3.1).
    """
    mapper = sa_inspect(Category)
    column_keys = {column.key for column in mapper.columns}

    assert column_keys == {"category_id", "name"}


def test_category_primary_key():
    """
    category_id es la clave primaria única de Category.
    """
    mapper = sa_inspect(Category)
    pk_keys = {column.key for column in mapper.primary_key}

    assert pk_keys == {"category_id"}


def test_lexical_item_has_expected_columns():
    """
    LexicalItem debe tener exactamente:
      - lexical_item_id
      - category_id
      - text
    según ET v1.4 y MP-DATA-01.
    """
    mapper = sa_inspect(LexicalItem)
    column_keys = {column.key for column in mapper.columns}

    assert column_keys == {"lexical_item_id", "category_id", "text"}


def test_lexical_item_primary_key():
    """
    lexical_item_id es la clave primaria única de LexicalItem.
    """
    mapper = sa_inspect(LexicalItem)
    pk_keys = {column.key for column in mapper.primary_key}

    assert pk_keys == {"lexical_item_id"}


def test_lexical_item_has_foreign_key_to_category():
    """
    LexicalItem.category_id debe ser FK hacia Category,
    sin asumir nombres concretos de tabla.
    """
    mapper = sa_inspect(LexicalItem)
    category_id_col = mapper.columns["category_id"]
    foreign_keys = list(category_id_col.foreign_keys)

    # Debe existir exactamente una FK asociada a category_id.
    assert len(foreign_keys) == 1


def test_category_lexical_items_relationship_exists():
    """
    ET v1.4 + MP-DATA-01 implican una relación Category ↔ LexicalItem.
    Verificamos que los atributos de relación existen.
    """
    assert hasattr(Category, "lexical_items"), "Category debe exponer .lexical_items"
    assert hasattr(LexicalItem, "category"), "LexicalItem debe exponer .category"


def test_can_instantiate_category_and_lexical_item_in_memory():
    """
    Comprobación simple de que los modelos se pueden instanciar
    y enlazar en memoria sin necesidad de base de datos.
    """
    category = Category(name="Compras en supermercado")
    item = LexicalItem(category=category, text="Beispiel")

    assert item.category is category
    assert item.text == "Beispiel"
