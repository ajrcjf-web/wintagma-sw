from alembic import op
import sqlalchemy as sa

# Revisiones
revision = "0003_load_initial_content"
down_revision = "0002_create_exercise_and_exercise_option"
branch_labels = None
depends_on = None


def upgrade() -> None:
    category_table = sa.table(
        "category",
        sa.column("category_id", sa.Integer),
        sa.column("name", sa.String),
    )

    lexical_item_table = sa.table(
        "lexical_item",
        sa.column("lexical_item_id", sa.Integer),
        sa.column("category_id", sa.Integer),
        sa.column("text", sa.String),
    )

    # Categorías normativas (según ET v1.4)
    op.bulk_insert(
        category_table,
        [
            {"category_id": 1, "name": "Compras en supermercado"},
            {"category_id": 2, "name": "Reunión de trabajo"},
        ],
    )

    # Lexical items — categoría 1: Compras en supermercado (1–30) — alemán
    op.bulk_insert(
        lexical_item_table,
        [
            # Palabras / términos técnicos
            {"lexical_item_id": 1, "category_id": 1, "text": "Barcode"},
            {"lexical_item_id": 2, "category_id": 1, "text": "Kassenbon"},
            {"lexical_item_id": 3, "category_id": 1, "text": "Rabatt"},
            {"lexical_item_id": 4, "category_id": 1, "text": "Kassierer"},
            {"lexical_item_id": 5, "category_id": 1, "text": "Einkaufswagen"},
            {"lexical_item_id": 6, "category_id": 1, "text": "Gang"},
            {"lexical_item_id": 7, "category_id": 1, "text": "Regal"},
            {"lexical_item_id": 8, "category_id": 1, "text": "Scanner"},
            {"lexical_item_id": 9, "category_id": 1, "text": "Inventur"},
            {"lexical_item_id": 10, "category_id": 1, "text": "Lieferant"},
            {"lexical_item_id": 11, "category_id": 1, "text": "Verpackung"},
            {"lexical_item_id": 12, "category_id": 1, "text": "Lieferung"},
            {"lexical_item_id": 13, "category_id": 1, "text": "Tiefkühler"},
            {"lexical_item_id": 14, "category_id": 1, "text": "Kasse"},
            {"lexical_item_id": 15, "category_id": 1, "text": "Aktion"},
            {"lexical_item_id": 16, "category_id": 1, "text": "Gutschein"},
            {"lexical_item_id": 17, "category_id": 1, "text": "Bestandskontrolle"},
            {"lexical_item_id": 18, "category_id": 1, "text": "Preisliste"},
            {"lexical_item_id": 19, "category_id": 1, "text": "Stückpreis"},
            {"lexical_item_id": 20, "category_id": 1, "text": "Mindesthaltbarkeitsdatum"},

            # Sintagmas nominales técnicos (categoría 1)
            {"lexical_item_id": 21, "category_id": 1, "text": "Sonderangebot"},
            {"lexical_item_id": 22, "category_id": 1, "text": "Preisschild"},
            {"lexical_item_id": 23, "category_id": 1, "text": "Tiefkühlabteilung"},
            {"lexical_item_id": 24, "category_id": 1, "text": "Frischeabteilung"},
            {"lexical_item_id": 25, "category_id": 1, "text": "Kundendienst"},

            # Más términos simples para completar 30
            {"lexical_item_id": 26, "category_id": 1, "text": "Kundenkarte"},
            {"lexical_item_id": 27, "category_id": 1, "text": "Online-Bestellung"},
            {"lexical_item_id": 28, "category_id": 1, "text": "Papiertüte"},
            {"lexical_item_id": 29, "category_id": 1, "text": "Einkaufskorb"},
            {"lexical_item_id": 30, "category_id": 1, "text": "Filialleiter"},
        ],
    )

    # Lexical items — categoría 2: Reunión de trabajo (31–60) — alemán
    op.bulk_insert(
        lexical_item_table,
        [
            # Palabras / términos técnicos
            {"lexical_item_id": 31, "category_id": 2, "text": "Tagesordnung"},
            {"lexical_item_id": 32, "category_id": 2, "text": "Protokoll"},
            {"lexical_item_id": 33, "category_id": 2, "text": "Frist"},
            {"lexical_item_id": 34, "category_id": 2, "text": "Budget"},
            {"lexical_item_id": 35, "category_id": 2, "text": "Projektplan"},
            {"lexical_item_id": 36, "category_id": 2, "text": "Feedback"},
            {"lexical_item_id": 37, "category_id": 2, "text": "Präsentation"},
            {"lexical_item_id": 38, "category_id": 2, "text": "Foliensatz"},
            {"lexical_item_id": 39, "category_id": 2, "text": "Zeitfenster"},
            {"lexical_item_id": 40, "category_id": 2, "text": "Aufgabenliste"},
            {"lexical_item_id": 41, "category_id": 2, "text": "Brainstorming"},
            {"lexical_item_id": 42, "category_id": 2, "text": "Status-Update"},
            {"lexical_item_id": 43, "category_id": 2, "text": "Telefonkonferenz"},
            {"lexical_item_id": 44, "category_id": 2, "text": "Videokonferenz"},
            {"lexical_item_id": 45, "category_id": 2, "text": "Schulung"},

            # Sintagmas nominales técnicos (categoría 2)
            {"lexical_item_id": 46, "category_id": 2, "text": "Projektstart-Meeting"},
            {"lexical_item_id": 47, "category_id": 2, "text": "Maßnahmenliste"},
            {"lexical_item_id": 48, "category_id": 2, "text": "Raumbuchung"},
            {"lexical_item_id": 49, "category_id": 2, "text": "Besprechungsprotokoll"},
            {"lexical_item_id": 50, "category_id": 2, "text": "Projektumfang-Dokument"},

            # Términos adicionales para completar 60
            {"lexical_item_id": 51, "category_id": 2, "text": "Risikoübersicht"},
            {"lexical_item_id": 52, "category_id": 2, "text": "Entscheidungsprotokoll"},
            {"lexical_item_id": 53, "category_id": 2, "text": "Kernpunkt"},
            {"lexical_item_id": 54, "category_id": 2, "text": "Remote-Teilnehmer"},
            {"lexical_item_id": 55, "category_id": 2, "text": "Meeting-Erinnerung"},
            {"lexical_item_id": 56, "category_id": 2, "text": "Projektzeitplan"},
            {"lexical_item_id": 57, "category_id": 2, "text": "Follow-up-E-Mail"},
            {"lexical_item_id": 58, "category_id": 2, "text": "Mitarbeitergespräch"},
            {"lexical_item_id": 59, "category_id": 2, "text": "Wochenmeeting"},
            {"lexical_item_id": 60, "category_id": 2, "text": "Implementierungsplan"},
        ],
    )


def downgrade() -> None:
    conn = op.get_bind()

    # Eliminar lexical items de estas dos categorías
    conn.execute(
        sa.text(
            "DELETE FROM lexical_item WHERE category_id IN (:c1, :c2)"
        ),
        {"c1": 1, "c2": 2},
    )

    # Eliminar categorías insertadas
    conn.execute(
        sa.text(
            "DELETE FROM category WHERE category_id IN (:c1, :c2)"
        ),
        {"c1": 1, "c2": 2},
    )
