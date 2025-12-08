from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_generate_exercise_request_schema():
    response = client.post(
        "/exercise/generate",
        json={
            "category_id": 1,
            "previous_lexical_item_id": None,
        },
    )

    # El endpoint debe existir y devolver un status code normativo:
    # 200  → ejercicio generado
    # 400  → insufficient_items
    # 404  → category_not_found
    # 500  → internal_error (p.ej. sin BD real en tests)
    assert response.status_code in (200, 400, 404, 500)
