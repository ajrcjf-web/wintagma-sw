package com.wintagma.android.feature.exercise

import com.wintagma.android.domain.model.Exercise

/**
 * Estados de la pantalla de ejercicio, alineados con la ET v1.4:
 * - ShowingExercise
 * - ShowingFeedback
 */
sealed class ExerciseScreenState {

    /**
     * Estado opcional de carga inicial.
     * No es obligatorio en la ET, pero es útil para la UI.
     */
    data object Loading : ExerciseScreenState()

    /**
     * Estado mostrando el ejercicio actual.
     */
    data class ShowingExercise(
        val exercise: Exercise
    ) : ExerciseScreenState()

    /**
     * Estado mostrando el feedback del ejercicio ya respondido.
     *
     * Se exponen directamente los campos normativos de resultado:
     * - isCorrect (derivado de correct)
     * - correctOptionId (correct_option_id)
     * - scoreDelta (score_delta)
     */
    data class ShowingFeedback(
        val exercise: Exercise,
        val isCorrect: Boolean,
        val correctOptionId: Int,
        val scoreDelta: Int
    ) : ExerciseScreenState()
}
