package com.wintagma.android.feature.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wintagma.android.domain.model.ExerciseOption

/**
 * Pantalla de ejercicio.
 *
 * Flujo:
 *  - ShowingExercise: muestra prompt + opciones.
 *  - ShowingFeedback: muestra feedback + botón "Siguiente ejercicio".
 *
 * No contiene lógica de negocio: delega en callbacks.
 */
@Composable
fun ExerciseScreen(
    state: ExerciseScreenState?,
    onOptionSelected: (Int) -> Unit,
    onNextExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        when (val current = state) {
            null,
            is ExerciseScreenState.Loading -> {
                LoadingContent()
            }

            is ExerciseScreenState.ShowingExercise -> {
                ExerciseContent(
                    prompt = current.exercise.prompt,
                    options = current.exercise.options,
                    onOptionSelected = onOptionSelected
                )
            }

            is ExerciseScreenState.ShowingFeedback -> {
                FeedbackContent(
                    prompt = current.exercise.prompt,
                    options = current.exercise.options,
                    isCorrect = current.isCorrect,
                    scoreDelta = current.scoreDelta,
                    onNextExercise = onNextExercise
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "Cargando ejercicio…",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ExerciseContent(
    prompt: String,
    options: List<ExerciseOption>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option.option_id) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackContent(
    prompt: String,
    options: List<ExerciseOption>,
    isCorrect: Boolean,
    scoreDelta: Int,
    onNextExercise: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.titleLarge
        )

        // Mensaje de feedback mínimo, alineado con score_delta ∈ {0,1}
        val feedbackTitle = if (isCorrect) {
            "¡Correcto!"
        } else {
            "Incorrecto"
        }

        val feedbackDescription = if (scoreDelta == 1) {
            "Has obtenido 1 punto por esta respuesta."
        } else {
            "En esta respuesta no obtuviste puntos."
        }

        Text(
            text = feedbackTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = feedbackDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            // Mostramos las opciones de forma pasiva (sin interacción)
            options.forEach { option ->
                Button(
                    onClick = { /* deshabilitado durante feedback */ },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Button(
            onClick = onNextExercise,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Siguiente ejercicio",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Formatea la etiqueta de una opción para mostrar en la UI.
 * @param index Índice basado en 0
 * @param text Texto de la opción
 * @return String formateado como "1. texto"
 */
fun formatOptionLabel(index: Int, text: String): String {
    return "${index + 1}. $text"
}

/**
 * Mensaje de feedback para mostrar al usuario.
 */
data class FeedbackMessage(
    val title: String,
    val subtitle: String
)

/**
 * Construye el mensaje de feedback basado en el resultado.
 * @param isCorrect Si la respuesta fue correcta
 * @param scoreDelta Cambio en el puntaje
 * @return Mensaje de feedback para mostrar al usuario
 */
fun buildFeedbackMessage(isCorrect: Boolean, scoreDelta: Int): FeedbackMessage {
    return if (isCorrect) {
        FeedbackMessage(
            title = "¡Correcto!",
            subtitle = "Puntuación +$scoreDelta"
        )
    } else {
        FeedbackMessage(
            title = "Incorrecto",
            subtitle = "Puntuación +$scoreDelta"
        )
    }
}
