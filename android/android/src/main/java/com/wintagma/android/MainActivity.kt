package com.wintagma.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.domain.model.Exercise
import com.wintagma.android.domain.model.ExerciseOption
import com.wintagma.android.domain.model.ExerciseValidationResult
import com.wintagma.android.feature.category.CategorySelectionViewModel
import com.wintagma.android.feature.exercise.ExerciseProvider
import com.wintagma.android.feature.exercise.ExerciseViewModel
import com.wintagma.android.ui.theme.WintagmaTheme

// TODO: MP-INT-01 - Reemplazar por implementación real con HTTP
private class TemporaryExerciseProvider : ExerciseProvider {
    private val dummyExercises = listOf(
        Exercise(
            exercise_id = 1,
            category_id = 1,
            lexical_item_id = 1,
            prompt = "¿Cómo se dice 'perro' en inglés?",
            options = listOf(
                ExerciseOption(option_id = 1, text = "dog"),
                ExerciseOption(option_id = 2, text = "cat"),
                ExerciseOption(option_id = 3, text = "bird")
            )
        ),
        Exercise(
            exercise_id = 2,
            category_id = 1,
            lexical_item_id = 2,
            prompt = "¿Cómo se dice 'gato' en inglés?",
            options = listOf(
                ExerciseOption(option_id = 4, text = "dog"),
                ExerciseOption(option_id = 5, text = "cat"),
                ExerciseOption(option_id = 6, text = "fish")
            )
        )
    )
    
    private var currentIndex = 0

    override fun generateExercise(categoryId: Int, previousLexicalItemId: Int?): Exercise {
        val exercise = dummyExercises[currentIndex % dummyExercises.size]
        currentIndex++
        return exercise.copy(category_id = categoryId)
    }

    override fun validateExercise(exerciseId: Int, selectedOptionId: Int): ExerciseValidationResult {
        // Opciones correctas: 1 para "dog", 5 para "cat"
        val isCorrect = selectedOptionId == 1 || selectedOptionId == 5
        return ExerciseValidationResult(
            correct = isCorrect,
            correct_option_id = if (exerciseId == 1) 1 else 5,
            score_delta = if (isCorrect) 10 else 0
        )
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WintagmaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: MP-INT-01 - Inyectar provider real
                    val contentRepository = ContentRepository()
                    val categoryViewModel = CategorySelectionViewModel(contentRepository)
                    val exerciseViewModel = ExerciseViewModel(TemporaryExerciseProvider())
                    WintagmaAppRoot(
                        categoryViewModel = categoryViewModel,
                        exerciseViewModel = exerciseViewModel
                    )
                }
            }
        }
    }
}
