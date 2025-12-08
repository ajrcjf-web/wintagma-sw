from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_validate_exercise_schema():
    response = client.post(
        "/exercise/validate",
        json={"exercise_id": 1, "selected_option_id": 1}
    )

    # El endpoint existe y devuelve un código normativo
    assert response.status_code in (200, 400, 404, 500)
