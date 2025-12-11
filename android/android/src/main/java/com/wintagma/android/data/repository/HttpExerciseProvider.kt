package com.wintagma.android.data.repository

import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseOption
import com.wintagma.android.domain.model.ExerciseValidationResult
import com.wintagma.android.feature.exercise.ExerciseProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Implementación real de ExerciseProvider que habla con el backend FastAPI
 * usando los endpoints normativos:
 *
 *  - POST /exercise/generate
 *  - POST /exercise/validate
 */
class HttpExerciseProvider(
    private val baseUrl: String = DEFAULT_BASE_URL
) : ExerciseProvider {

    // POST /exercise/generate
    override fun generateExercise(
        categoryId: Int,
        previousLexicalItemId: Int?
    ): Exercise {
        val payload = JSONObject().apply {
            put("category_id", categoryId)
            // el modelo del backend espera el campo, aunque sea null
            if (previousLexicalItemId != null) {
                put("previous_lexical_item_id", previousLexicalItemId)
            } else {
                put("previous_lexical_item_id", JSONObject.NULL)
            }
        }

        val body = httpPostJson("/exercise/generate", payload)
        return parseExerciseJson(body, categoryId, previousLexicalItemId ?: 0)
    }

    // POST /exercise/validate
    override fun validateExercise(
        exerciseId: Int,
        selectedOptionId: Int
    ): ExerciseValidationResult {
        val payload = JSONObject().apply {
            put("exercise_id", exerciseId)
            put("selected_option_id", selectedOptionId)
        }

        val body = httpPostJson("/exercise/validate", payload)
        return parseExerciseValidationJson(body)
    }

    /**
     * POST JSON bloqueante, mismo patrón de errores que ContentRepository.
     */
    @Throws(ApiException::class)
    protected open fun httpPostJson(
        path: String,
        payload: JSONObject
    ): String {
        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val url = URL("$normalizedBase$normalizedPath")

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = DEFAULT_CONNECT_TIMEOUT_MS
            readTimeout = DEFAULT_READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        return try {
            // Enviar body JSON en UTF-8
            BufferedWriter(OutputStreamWriter(connection.outputStream, Charsets.UTF_8)).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

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
         * Debe ser consistente con ContentRepository.DEFAULT_BASE_URL.
         */
        const val DEFAULT_BASE_URL: String = "http://10.0.2.2:8000"

        private const val DEFAULT_CONNECT_TIMEOUT_MS: Int = 5_000
        private const val DEFAULT_READ_TIMEOUT_MS: Int = 5_000
    }
}

/**
 * Leer InputStream completo como String en UTF-8.
 */
private fun java.io.InputStream.bufferedReaderUse(): String =
    BufferedReader(InputStreamReader(this, Charsets.UTF_8)).use { it.readText() }

/**
 * ---- PARSEO JSON ↔ MODELOS DE DOMINIO ----
 */

// /exercise/generate → Exercise
fun parseExerciseJson(body: String, categoryId: Int, lexicalItemId: Int): Exercise {
    if (body.isBlank()) {
        throw IllegalArgumentException("Empty body for exercise")
    }

    val root = JSONObject(body)

    val exerciseId = root.getInt("exercise_id")
    val prompt = root.getString("prompt")
    val optionsArray: JSONArray = root.getJSONArray("options")

    val options = mutableListOf<ExerciseOption>()
    for (i in 0 until optionsArray.length()) {
        val item = optionsArray.getJSONObject(i)
        val optionId = item.getInt("option_id")
        val text = item.getString("text")

        options += ExerciseOption(
            option_id = optionId,
            text = text
        )
    }

    return Exercise(
        exercise_id = exerciseId,
        category_id = categoryId,
        lexical_item_id = lexicalItemId,
        prompt = prompt,
        options = options
    )
}

// /exercise/validate → ExerciseValidationResult
fun parseExerciseValidationJson(body: String): ExerciseValidationResult {
    if (body.isBlank()) {
        throw IllegalArgumentException("Empty body for validation result")
    }

    val root = JSONObject(body)
    val correct = root.getBoolean("correct")
    val correctOptionId = root.getInt("correct_option_id")
    val scoreDelta = root.getInt("score_delta")

    return ExerciseValidationResult(
        correct = correct,
        correct_option_id = correctOptionId,
        score_delta = scoreDelta
    )
}
