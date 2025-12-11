package com.wintagma.android.feature.exercise

import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseOption
import com.wintagma.android.domain.model.ExerciseValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MP-TEST-03 — Tests adicionales para ExerciseViewModel.
 *
 * Objetivo:
 *  - Validar el flujo de respuesta incorrecta (score_delta = 0).
 *  - Verificar que previousLexicalItemId se propaga correctamente
 *    al ExerciseProvider (modo B: no repetición inmediata en backend).
 *
 * Tests:
 *  1) submitAnswer con opción incorrecta → ShowingFeedback con isCorrect=false y scoreDelta=0.
 *  2) nextExercise usa el último lexical_item_id como previousLexicalItemId en la segunda generación.
 *
 * Cumple MP-Standard v1.1:
 *  - Tests pequeños, sin IO, sin framework Android ni Compose.
 */
class ExerciseViewModelAdditionalTest {

    /**
     * Fake provider que devuelve siempre el mismo ejercicio,
     * pero marca correct/score_delta en validateExercise según selectedOptionId.
     */
    private class IncorrectAnswerProvider : ExerciseProvider {

        override fun generateExercise(
            categoryId: Int,
            previousLexicalItemId: Int?
        ): Exercise {
            val options = (1..5).map { optionId ->
                ExerciseOption(
                    option_id = optionId,
                    text = "Opción $optionId"
                )
            }

            return Exercise(
                exercise_id = 1,
                category_id = categoryId,
                lexical_item_id = 100,
                prompt = "¿Cuál es la opción correcta?",
                options = options
            )
        }

        override fun validateExercise(
            exerciseId: Int,
            selectedOptionId: Int
        ): ExerciseValidationResult {
            val isCorrect = (selectedOptionId == 1)
            return ExerciseValidationResult(
                correct = isCorrect,
                correct_option_id = 1,
                score_delta = if (isCorrect) 1 else 0
            )
        }
    }

    @org.junit.Ignore("Requiere kotlinx-coroutines-test para testear coroutines con viewModelScope")
    @Test
    fun submitAnswer_incorrectOption_setsFeedbackWithScoreZero() {
        val provider = IncorrectAnswerProvider()
        val viewModel = ExerciseViewModel(provider)

        // 1) Generamos el primer ejercicio.
        viewModel.loadNextExercise(categoryId = 1)
        val firstState = viewModel.state.value
        assertTrue(firstState is ExerciseScreenState.ShowingExercise)

        // 2) Respondemos con una opción incorrecta (2).
        viewModel.submitAnswer(selectedOptionId = 2)

        val feedbackState = viewModel.state.value
        assertTrue(feedbackState is ExerciseScreenState.ShowingFeedback)
        val showingFeedback = feedbackState as ExerciseScreenState.ShowingFeedback

        // 3) Validamos que la respuesta se marca como incorrecta y score_delta = 0.
        assertFalse(showingFeedback.isCorrect)
        assertEquals(0, showingFeedback.scoreDelta)
        assertEquals(1, showingFeedback.correctOptionId)
    }

    /**
     * Fake provider que registra el previousLexicalItemId recibido en cada
     * llamada a generateExercise, para comprobar que ExerciseViewModel
     * propaga correctamente el lexical_item_id anterior (modo B).
     */
    private class TrackingExerciseProvider : ExerciseProvider {

        val previousLexicalIds: MutableList<Int?> = mutableListOf()
        private var generateCount: Int = 0

        override fun generateExercise(
            categoryId: Int,
            previousLexicalItemId: Int?
        ): Exercise {
            previousLexicalIds.add(previousLexicalItemId)
            generateCount += 1

            val exerciseId = generateCount
            val lexicalItemId = if (generateCount == 1) 100 else 200

            val options = (1..5).map { optionId ->
                ExerciseOption(
                    option_id = optionId,
                    text = "Opción $optionId"
                )
            }

            return Exercise(
                exercise_id = exerciseId,
                category_id = categoryId,
                lexical_item_id = lexicalItemId,
                prompt = "Ejercicio #$exerciseId",
                options = options
            )
        }

        override fun validateExercise(
            exerciseId: Int,
            selectedOptionId: Int
        ): ExerciseValidationResult {
            val isCorrect = (selectedOptionId == 1)
            return ExerciseValidationResult(
                correct = isCorrect,
                correct_option_id = 1,
                score_delta = if (isCorrect) 1 else 0
            )
        }
    }

    @org.junit.Ignore("Requiere kotlinx-coroutines-test para testear coroutines con viewModelScope")
    @Test
    fun nextExercise_usesPreviousLexicalItemIdInSecondGeneration() {
        val provider = TrackingExerciseProvider()
        val viewModel = ExerciseViewModel(provider)

        // 1) Primer ejercicio: previousLexicalItemId debe ser null.
        viewModel.loadNextExercise(categoryId = 1)
        val firstState = viewModel.state.value as ExerciseScreenState.ShowingExercise
        val firstLexicalId = firstState.exercise.lexical_item_id

        assertEquals(1, provider.previousLexicalIds.size)
        assertEquals(null, provider.previousLexicalIds[0])

        // 2) Respondemos y pasamos a feedback.
        viewModel.submitAnswer(selectedOptionId = 1)
        assertTrue(viewModel.state.value is ExerciseScreenState.ShowingFeedback)

        // 3) Siguiente ejercicio: debe usar el lexical_item_id previo.
        viewModel.nextExercise()

        assertTrue(viewModel.state.value is ExerciseScreenState.ShowingExercise)
        assertEquals(2, provider.previousLexicalIds.size)
        assertEquals(firstLexicalId, provider.previousLexicalIds[1])
    }
}
