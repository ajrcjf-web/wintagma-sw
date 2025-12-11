package com.wintagma.android.data.repository

import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseOption
import com.wintagma.android.domain.model.ExerciseValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests JVM pequeños (sin red real) para validar el parseo
 * de las respuestas JSON de /exercise/generate y /exercise/validate.
 *
 * Cumple MP-Standard:
 *  - test pequeño
 *  - sin IO ni framework Android
 *  - usa solo modelos ya existentes
 */
class ExerciseRemoteProviderParsingTest {

    @Test
    fun parseExerciseJson_mapsFieldsCorrectly() {
        val json = """
            {
              "exercise_id": 5001,
              "prompt": "Wie sagt man 'tornillo'?",
              "options": [
                { "option_id": 1, "text": "Schraube" },
                { "option_id": 2, "text": "Mutter" },
                { "option_id": 3, "text": "Nagel" },
                { "option_id": 4, "text": "Zange" },
                { "option_id": 5, "text": "Hammer" }
              ]
            }
        """.trimIndent()

        val exercise: Exercise = parseExerciseJson(json, categoryId = 1, lexicalItemId = 100)

        assertEquals(5001, exercise.exercise_id)
        assertEquals(1, exercise.category_id)
        assertEquals(100, exercise.lexical_item_id)
        assertEquals("Wie sagt man 'tornillo'?", exercise.prompt)
        assertEquals(5, exercise.options.size)

        val first: ExerciseOption = exercise.options.first()
        assertEquals(1, first.option_id)
        assertEquals("Schraube", first.text)
    }

    @Test
    fun parseExerciseValidationJson_mapsFieldsCorrectly() {
        val json = """
            {
              "correct": true,
              "correct_option_id": 2,
              "score_delta": 1
            }
        """.trimIndent()

        val result: ExerciseValidationResult = parseExerciseValidationJson(json)

        assertTrue(result.correct)
        assertEquals(2, result.correct_option_id)
        assertEquals(1, result.score_delta)
    }
}
