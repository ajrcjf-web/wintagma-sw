package com.wintagma.android.feature.exercise

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test mínimo de MP-APP-04.
 *
 * Valida la función pura buildFeedbackMessage usada en la UI de feedback.
 * Cumple MP-Standard v1.1: test pequeño, sin IO, sin framework Android. :contentReference[oaicite:9]{index=9}
 */
class ExerciseFeedbackUtilsTest {

    @Test
    fun buildFeedbackMessage_correctAnswer_returnsPositiveMessage() {
        val message = buildFeedbackMessage(
            isCorrect = true,
            scoreDelta = 1
        )

        assertEquals("¡Correcto!", message.title)
        assertEquals("Puntuación +1", message.subtitle)
    }
}
