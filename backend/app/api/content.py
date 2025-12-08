# backend/app/api/content.py
from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse

from app.core.db import SessionLocal
from app.models.category import Category
from app.models.lexical_item import LexicalItem
from app.schemas.category import CategoryListResponse
from app.schemas.lexical_item import LexicalItemListResponse

router = APIRouter()


@router.get("/categories", response_model=CategoryListResponse)
def get_categories():
    """
    GET /content/categories

    Respuesta normativa según ET v1.4 (cap. 6.2):
    {
      "categories": [
        { "category_id": 1, "name": "Compras en supermercado" },
        { "category_id": 2, "name": "Reunión de trabajo" }
      ]
    }
    """
    db = SessionLocal()
    try:
        categories = db.query(Category).all()
        return {"categories": categories}
    except Exception:
        # Se mantiene internal_error como en MP-API-01
        raise HTTPException(status_code=500, detail="internal_error")
    finally:
        db.close()


@router.get(
    "/items/{category_id}",
    response_model=LexicalItemListResponse,
)
def get_items(category_id: int):
    """
    GET /content/items/{category_id}

    Respuesta 200 normativa según ET v1.4 (cap. 6.3):

    {
      "items": [
        { "lexical_item_id": 101, "category_id": 1, "text": "bolt" }
      ]
    }

    Errores normativos:
      - { "error": "category_not_found" }
      - { "error": "insufficient_items" }
    """
    db = SessionLocal()
    try:
        # 1) Verificar existencia de la categoría
        category = (
            db.query(Category)
            .filter(Category.category_id == category_id)
            .first()
        )
        if category is None:
            # Error estándar de la ET: { "error": "category_not_found" }
            return JSONResponse(
                status_code=404,
                content={"error": "category_not_found"},
            )

        # 2) Obtener items de la categoría
        items = (
            db.query(LexicalItem)
            .filter(LexicalItem.category_id == category_id)
            .all()
        )

        # Regla mínima: si no hay items → insufficient_items
        if not items:
            return JSONResponse(
                status_code=400,
                content={"error": "insufficient_items"},
            )

        # 3) Respuesta normativa
        return {"items": items}
    except Exception:
        # Error genérico interno
        return JSONResponse(
            status_code=500,
            content={"error": "internal_error"},
        )
    finally:
        db.close()
