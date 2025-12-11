package com.wintagma.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.data.repository.HttpExerciseProvider
import com.wintagma.android.feature.category.CategorySelectionViewModel
import com.wintagma.android.feature.exercise.ExerciseViewModel
import com.wintagma.android.ui.theme.WintagmaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WintagmaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val contentRepository = ContentRepository()
                    val categoryViewModel = CategorySelectionViewModel(contentRepository)

                    // ✅ Provider REAL hacia FastAPI
                    val exerciseProvider = HttpExerciseProvider()
                    val exerciseViewModel = ExerciseViewModel(exerciseProvider)

                    WintagmaAppRoot(
                        categoryViewModel = categoryViewModel,
                        exerciseViewModel = exerciseViewModel
                    )
                }
            }
        }
    }
}
