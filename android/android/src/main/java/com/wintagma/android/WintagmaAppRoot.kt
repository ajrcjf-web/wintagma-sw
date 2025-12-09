package com.wintagma.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.feature.category.CategorySelectionScreen
import com.wintagma.android.feature.category.CategorySelectionViewModel
import com.wintagma.android.feature.exercise.ExerciseScreen
import com.wintagma.android.feature.exercise.ExerciseScreenState
import com.wintagma.android.feature.exercise.ExerciseViewModel

/**
 * Pantallas de alto nivel de la app.
 *
 * No introduce navegación compleja ni librerías extra:
 * solo dos pantallas:
 *  - CategorySelection
 *  - Exercise
 */
enum class RootScreen {
    CategorySelection,
    Exercise
}

/**
 * Estado mínimo de navegación de la app.
 *
 * Es una clase de dominio puro (sin Compose ni Android),
 * así puede ser testeada en JVM.
 */
data class AppNavigationState(
    val currentScreen: RootScreen = RootScreen.CategorySelection,
    val selectedCategoryId: Int? = null
) {
    fun onCategorySelected(categoryId: Int): AppNavigationState =
        copy(
            currentScreen = RootScreen.Exercise,
            selectedCategoryId = categoryId
        )
}

/**
 * Composable raíz de la app.
 *
 * - Arranca en selección de categorías.
 * - Al seleccionar categoría:
 *     1) actualiza AppNavigationState
 *     2) pide al ExerciseViewModel que cargue el siguiente ejercicio
 * - En pantalla de ejercicio:
 *     delega en ExerciseViewModel la gestión de estado y respuestas.
 */
@Composable
fun WintagmaAppRoot(
    categoryViewModel: CategorySelectionViewModel,
    exerciseViewModel: ExerciseViewModel
) {
    var navState by remember { mutableStateOf(AppNavigationState()) }

    when (navState.currentScreen) {
        RootScreen.CategorySelection -> {
            // CategorySelectionScreen (APP-02) expone onCategorySelected(categoryId: Int)
            CategorySelectionScreen(
                viewModel = categoryViewModel,
                onCategorySelected = { categoryId ->
                    // 1) actualizar navegación
                    navState = navState.onCategorySelected(categoryId)
                    // 2) disparar carga de ejercicio en el ViewModel
                    exerciseViewModel.loadNextExercise(categoryId)
                }
            )
        }

        RootScreen.Exercise -> {
            // NOTA:
            // Aquí asumo que ExerciseViewModel expone algo tipo:
            //   val state: ExerciseScreenState?
            // Si en tu repo es uiState o similar, ajusta el nombre.
            val screenState: ExerciseScreenState? by exerciseViewModel.state

            ExerciseScreen(
                state = screenState,
                onOptionSelected = { optionId ->
                    // Delegamos en el ViewModel la lógica ya implementada en APP-05
                    exerciseViewModel.submitAnswer(optionId)
                },
                onNextExercise = {
                    // Solicita siguiente ejercicio
                    navState.selectedCategoryId?.let { categoryId ->
                        exerciseViewModel.loadNextExercise(categoryId)
                    }
                }
            )
        }
    }
}
