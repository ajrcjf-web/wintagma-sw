package com.wintagma.android.domain.model

/**
 * Respuesta de POST /exercise/validate según ET v1.4:
 *
 * {
 *   "correct": true,
 *   "correct_option_id": 2,
 *   "score_delta": 1
 * }
 */
data class ExerciseValidationResult(
    val correct: Boolean,
    val correct_option_id: Int,
    val score_delta: Int,
)
