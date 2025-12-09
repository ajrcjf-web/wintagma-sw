package com.wintagma.android.feature.exercise

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test mínimo de MP-APP-03:
 * valida la función pura formatOptionLabel usada por la UI de ExerciseScreen.
 *
 * Cumple MP-Standard:
 * - Test pequeño
 * - Sin IO
 * - Independiente del framework Android / Compose
 */
class ExerciseScreenStateTest {

    @Test
    fun formatOptionLabel_startsIndexAtOne() {
        val result = formatOptionLabel(index = 0, text = "Hallo")
        assertEquals("1. Hallo", result)
    }
}
