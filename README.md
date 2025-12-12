# Wintagma SW — MVP v1.0.0

Repositorio oficial del **MVP técnico de Wintagma SW**, un sistema educativo de ejercicios léxicos con arquitectura **Android + Backend FastAPI**, desarrollado bajo el **Estándar de Desarrollo de Software Asistido por IA (Wintagma / NtagMA)** y gestionado mediante **MP-Units**.

---

## 🧭 Visión general

Wintagma SW es un MVP educativo que permite:

- Seleccionar una categoría léxica.
- Generar ejercicios de opción múltiple (5 opciones).
- Validar respuestas de forma determinista.
- Aplicar la regla pedagógica de **no repetición inmediata (Modo B)**.

El sistema está diseñado para ser:

- Simple
- Determinista
- Sin login ni tracking de usuario
- Totalmente alineado con una Especificación Técnica cerrada

---

## 🏗️ Arquitectura

```
Android App (Kotlin / Jetpack Compose)
        ↓ HTTP (JSON)
Backend API (FastAPI)
        ↓
PostgreSQL
```

### Backend
- **Framework:** FastAPI
- **ORM:** SQLAlchemy 2.0
- **Migraciones:** Alembic
- **Base de datos:** PostgreSQL 15
- **Testing:** Pytest

### Android
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose
- **Arquitectura:** MVVM
- **Networking:** HttpURLConnection (sin librerías externas)
- **Testing:** JVM unit tests (Gradle)

---

## 📁 Estructura del repositorio

```
backend/
  app/
    api/
    core/
    models/
    schemas/
  alembic/
  tests/

android/
  android/
    src/main/
    src/test/
```

---

## 🚀 Estado del proyecto

- **Versión:** v1.0.0
- **Estado:** MVP técnico cerrado
- **Tag oficial:** `v1.0.0`
- **Baseline:** congelado tras MP-TEST-04

Todos los MPs definidos en el cronograma oficial han sido ejecutados, validados y documentados mediante Implementation Reports.

---

## 🔌 Endpoints disponibles (Backend)

### Contenido
- `GET /content/categories`
- `GET /content/items/{category_id}`

### Ejercicios
- `POST /exercise/generate`
- `POST /exercise/validate`

Los contratos JSON, errores normativos y reglas pedagógicas están definidos **exclusivamente** en la Especificación Técnica v1.4.

---

## ▶️ Ejecución local (desarrollo)

### Backend

Requisitos:
- Python 3.11+
- Docker (para PostgreSQL)
- `uv`

```bash
cd backend
uv sync
uv run alembic upgrade head
uv run uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### Android

Requisitos:
- Android Studio
- Emulador **Google APIs x86_64** (recomendado)

```bash
cd android
./gradlew :android:assembleDebug
```

La app se conecta al backend local vía `http://10.0.2.2:8000`.

---

## 🧪 Tests

### Backend

```bash
cd backend
uv run pytest -q
```

### Android (tests JVM)

```bash
cd android
./gradlew :android:testDebugUnitTest
```

---

## 📜 Gobernanza y estándares

Este proyecto sigue estrictamente:

- Documento Maestro del Proyecto (DMP v1.3)
- Especificación Técnica Wintagma SW v1.4
- Estándar de Desarrollo de Software Asistido por IA v1.6
- MP-Standard v1.2

No se aceptan cambios fuera del baseline aprobado.

---

## 📦 Release

- **Release técnico:** v1.0.0
- **Artefactos:**
  - APK Debug Android
  - Backend FastAPI operativo

No se incluye despliegue en Play Store ni infraestructura de producción.

---

## ⚠️ Notas importantes

- El proyecto **no implementa autenticación**, perfiles de usuario ni analítica.
- No existe lógica adaptativa ni scoring acumulativo.
- El objetivo es validación técnica y pedagógica del MVP.

---

## 🏁 Cierre

Wintagma SW v1.0.0 representa un MVP completamente funcional, validado y cerrado desde el punto de vista técnico.

Cualquier evolución posterior (v1.1.0+) requiere un nuevo baseline aprobado por LTA.
