package com.wintagma.android.data.repository 

import com.wintagma.android.core.model.Category
import com.wintagma.android.core.model.LexicalItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repositorio de contenido que integra con el backend FastAPI
 * usando los endpoints normativos de la ET v1.4:
 *
 * - GET /content/categories
 * - GET /content/items/{category_id}
 *
 * La clase se mantiene `open` para permitir dobles/fakes en tests
 * (tal como ya se usa en MP-APP-02).
 */
open class ContentRepository(
    private val baseUrl: String = DEFAULT_BASE_URL
) {

    /**
     * Obtiene la lista de categorías desde el backend.
     *
     * Endpoint: GET /content/categories
     *
     * Respuesta esperada (ET 6.2):
     * {
     *   "categories": [
     *     { "category_id": 1, "name": "Compras en supermercado" },
     *     { "category_id": 2, "name": "Reunión de trabajo" }
     *   ]
     * }
     */
    open fun getCategories(): List<Category> {
        val body = httpGet("/content/categories")
        return parseCategoriesJson(body)
    }

    /**
     * Obtiene los lexical items de una categoría concreta.
     *
     * Endpoint: GET /content/items/{category_id}
     *
     * Respuesta esperada (ET 6.3):
     * {
     *   "items": [
     *     { "lexical_item_id": 101, "category_id": 1, "text": "bolt" }
     *   ]
     * }
     *
     * Errores normativos:
     * - category_not_found (404)
     * - insufficient_items (400)
     */
    open fun getLexicalItems(categoryId: Int): List<LexicalItem> {
        val body = httpGet("/content/items/$categoryId")
        return parseLexicalItemsJson(body)
    }

    /**
     * Llamada HTTP GET bloqueante mínima.
     *
     * No introduce librerías externas (usa HttpURLConnection
     * del SDK estándar de Android), y respeta el formato
     * de error normativo:
     *
     * { "error": "<codigo_error>" }
     */
    @Throws(ApiException::class)
    protected open fun httpGet(path: String): String {
        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val url = URL("$normalizedBase$normalizedPath")

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = DEFAULT_CONNECT_TIMEOUT_MS
            readTimeout = DEFAULT_READ_TIMEOUT_MS
        }

        return try {
            val status = connection.responseCode
            val stream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = stream?.bufferedReaderUse() ?: ""

            if (status in 200..299) {
                body
            } else {
                // Formato de error normativo: { "error": "<codigo_error>" }
                val errorCode = try {
                    val json = JSONObject(body)
                    json.optString("error", "unknown_error")
                } catch (_: Exception) {
                    "unknown_error"
                }
                throw ApiException(errorCode = errorCode, httpStatus = status)
            }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        /**
         * URL base de backend para entorno local/emulador.
         * Ajustable según tu entorno real sin cambiar contratos.
         *
         * Ejemplo típico de FastAPI local:
         *   http://10.0.2.2:8000
         */
        const val DEFAULT_BASE_URL: String = "http://10.0.2.2:8000"

        private const val DEFAULT_CONNECT_TIMEOUT_MS: Int = 5_000
        private const val DEFAULT_READ_TIMEOUT_MS: Int = 5_000
    }
}

/**
 * Excepción mínima para representar errores normativos
 * del backend en el cliente Android.
 *
 * No inventa nuevos códigos: reutiliza el campo "error"
 * y el status HTTP.
 */
class ApiException(
    val errorCode: String,
    val httpStatus: Int
) : Exception("API error: $errorCode (HTTP $httpStatus)")

/**
 * Función de apoyo para leer un InputStream como String
 * usando BufferedReader.
 */
private fun java.io.InputStream.bufferedReaderUse(): String =
    BufferedReader(InputStreamReader(this)).use { it.readText() }

/**
 * Función pura para mapear el JSON de categorías a modelos Kotlin.
 *
 * Se usa tanto en producción como en tests.
 */
fun parseCategoriesJson(body: String): List<Category> {
    if (body.isBlank()) return emptyList()

    val root = JSONObject(body)
    val categoriesArray: JSONArray = root.optJSONArray("categories") ?: return emptyList()
    val result = mutableListOf<Category>()

    for (i in 0 until categoriesArray.length()) {
        val item = categoriesArray.getJSONObject(i)
        val id = item.getInt("category_id")
        val name = item.getString("name")
        result += Category(
            categoryId = id,
            name = name
        )
    }

    return result
}

/**
 * Función pura para mapear el JSON de lexical items a modelos Kotlin.
 *
 * Se usa tanto en producción como en tests.
 */
fun parseLexicalItemsJson(body: String): List<LexicalItem> {
    if (body.isBlank()) return emptyList()

    val root = JSONObject(body)
    val itemsArray: JSONArray = root.optJSONArray("items") ?: return emptyList()
    val result = mutableListOf<LexicalItem>()

    for (i in 0 until itemsArray.length()) {
        val item = itemsArray.getJSONObject(i)
        val lexicalItemId = item.getInt("lexical_item_id")
        val categoryId = item.getInt("category_id")
        val text = item.getString("text")

        result += LexicalItem(
            lexicalItemId = lexicalItemId,
            categoryId = categoryId,
            text = text
        )
    }

    return result
}
