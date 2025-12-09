package com.wintagma.android.feature.exercise

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseValidationResult

/**
 * Proveedor de ejercicios abstracto.
 *
 * La implementación real (HTTP → backend) se hará en MP-INT-01.
 * En tests se usa un fake sin IO.
 */
interface ExerciseProvider {
    fun generateExercise(
        categoryId: Int,
        previousLexicalItemId: Int?
    ): Exercise

    fun validateExercise(
        exerciseId: Int,
        selectedOptionId: Int
    ): ExerciseValidationResult
}

/**
 * ViewModel de ExerciseScreen.
 *
 * Maneja el flujo:
 *  - loadNextExercise(categoryId) → ShowingExercise
 *  - submitAnswer(optionId)       → ShowingFeedback
 *  - nextExercise()               → nuevo ShowingExercise
 */
class ExerciseViewModel(
    private val provider: ExerciseProvider
) : ViewModel() {

    private val _state = mutableStateOf<ExerciseScreenState?>(null)
    val state: State<ExerciseScreenState?> = _state

    private var lastCategoryId: Int? = null
    private var lastLexicalItemId: Int? = null

    /**
     * Carga el siguiente ejercicio para la categoría dada.
     *
     * Usa previousLexicalItemId según lo definido en la ET (modo B),
     * pero la lógica concreta vive en el backend; aquí solo propagamos.
     */
    fun loadNextExercise(categoryId: Int) {
        lastCategoryId = categoryId

        val exercise = provider.generateExercise(
            categoryId = categoryId,
            previousLexicalItemId = lastLexicalItemId
        )

        // Guardamos el último lexical_item_id para modo B (no repetición inmediata).
        lastLexicalItemId = exercise.lexical_item_id

        _state.value = ExerciseScreenState.ShowingExercise(exercise)
    }

    /**
     * Envía la respuesta del usuario para el ejercicio actual y actualiza
     * el estado a ShowingFeedback.
     */
    fun submitAnswer(selectedOptionId: Int) {
        val current = _state.value
        if (current !is ExerciseScreenState.ShowingExercise) {
            return
        }

        val validation = provider.validateExercise(
            exerciseId = current.exercise.exercise_id,
            selectedOptionId = selectedOptionId
        )

        _state.value = ExerciseScreenState.ShowingFeedback(
            exercise = current.exercise,
            isCorrect = validation.correct,
            correctOptionId = validation.correct_option_id,
            scoreDelta = validation.score_delta
        )
    }

    /**
     * Flujo "siguiente ejercicio" completo:
     *
     * Desde ShowingFeedback → nuevo ShowingExercise,
     * reutilizando la última categoría seleccionada.
     *
     * Si no hay categoría previa, no hace nada (flujo defensivo).
     */
    fun nextExercise() {
        val categoryId = lastCategoryId ?: return
        loadNextExercise(categoryId)
    }
}
