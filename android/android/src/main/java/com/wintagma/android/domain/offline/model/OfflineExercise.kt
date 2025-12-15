package com.wintagma.android.domain.offline.model

/**
 * Modelo contractual del motor offline (Sección 6.6 / 6.8.10),
 * en versión paralela para no romper el modelo MVP existente.
 */
data class OfflineExercise(
    val categoryId: String,
    val exerciseId: String,        // "{categoryId}:{exerciseCounter}"
    val correctItemId: String,
    val options: List<String>,     // tamaño fijo = 4 (IDs)
    val correctOptionId: String,   // = correctItemId
)
