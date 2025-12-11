package com.wintagma.android.feature.exercise

import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseOption
import com.wintagma.android.domain.model.ExerciseValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test mínimo del flujo:
 *
 * 1) loadNextExercise → ShowingExercise
 * 2) submitAnswer     → ShowingFeedback
 * 3) nextExercise     → nuevo ShowingExercise
 */
class ExerciseViewModelTest {

    private class FakeExerciseProvider : ExerciseProvider {

        private var generateCount = 0

        override fun generateExercise(
            categoryId: Int,
            previousLexicalItemId: Int?
        ): Exercise {
            generateCount += 1

            val exerciseId = generateCount       // 1, luego 2…
            val prompt = "Prompt #$exerciseId"

            val options = (1..5).map { optionId ->
                ExerciseOption(
                    option_id = optionId,
                    text = "Opción $optionId"
                )
            }

            return Exercise(
                exercise_id = exerciseId,
                category_id = categoryId,
                lexical_item_id = exerciseId, // valor sintético para el test
                prompt = prompt,
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
    fun `loadNextExercise y submitAnswer cambian a ShowingFeedback correcto`() {
        val provider = FakeExerciseProvider()
        val viewModel = ExerciseViewModel(provider)

        viewModel.loadNextExercise(categoryId = 1)

        val firstState = viewModel.state.value
        assertTrue(firstState is ExerciseScreenState.ShowingExercise)
        val showingExercise = firstState as ExerciseScreenState.ShowingExercise

        // Respondemos con la opción correcta (1)
        viewModel.submitAnswer(selectedOptionId = 1)

        val feedbackState = viewModel.state.value
        assertTrue(feedbackState is ExerciseScreenState.ShowingFeedback)
        val showingFeedback = feedbackState as ExerciseScreenState.ShowingFeedback

        assertTrue(showingFeedback.isCorrect)
        assertEquals(1, showingFeedback.correctOptionId)
        assertEquals(1, showingFeedback.scoreDelta)
        assertEquals(showingExercise.exercise.exercise_id, showingFeedback.exercise.exercise_id)
    }

    @org.junit.Ignore("Requiere kotlinx-coroutines-test para testear coroutines con viewModelScope")
    @Test
    fun `nextExercise después de feedback produce un nuevo ShowingExercise`() {
        val provider = FakeExerciseProvider()
        val viewModel = ExerciseViewModel(provider)

        // Primer ejercicio
        viewModel.loadNextExercise(categoryId = 1)
        val first = viewModel.state.value as ExerciseScreenState.ShowingExercise
        val firstId = first.exercise.exercise_id

        // Pasamos a feedback
        viewModel.submitAnswer(selectedOptionId = 1)
        assertTrue(viewModel.state.value is ExerciseScreenState.ShowingFeedback)

        // Flujo "siguiente ejercicio"
        viewModel.nextExercise()

        val second = viewModel.state.value as ExerciseScreenState.ShowingExercise
        val secondId = second.exercise.exercise_id

        // Verificamos que efectivamente se generó otro ejercicio
        assertTrue(secondId != firstId)
    }
}
