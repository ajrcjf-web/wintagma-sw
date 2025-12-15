package com.wintagma.android.domain.offline.motor

import com.wintagma.android.domain.offline.model.OfflineExercise

/**
 * Implementación literal del algoritmo determinista v2.1 (Sección 6.8.5–6.8.10).
 *
 * - Estado por sesión (en memoria).
 * - Reset por cambio de categoría (modo "sesión por categoría" según ET).
 * - Determinismo: no usa random, tiempo, hilos, ni locale.
 */
class DeterministicOfflineExerciseMotor {

    private var activeCategoryId: String? = null
    private var state: MotorState = MotorState.initial()

    /**
     * Genera el próximo ejercicio para una categoría a partir de un snapshot lógico
     * de IDs de lexical items de esa categoría.
     *
     * Precondición: lexicalItemIds deben corresponder a un dataset válido (Room),
     * pero aquí operamos sobre IDs para mantener el motor puro.
     */
    fun generate(categoryId: String, lexicalItemIds: List<String>): MotorResult {
        return try {
            if (activeCategoryId != categoryId) {
                // Reset por cambio de categoría (ET 6.8.1 + 6.4)
                activeCategoryId = categoryId
                state = MotorState.initial()
            }

            val sortedItems = lexicalItemIds
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sortedWith(compareBy<String> { it }) // String.compareTo (Kotlin/JVM) = Unicode binario

            // 6.8.3 Precondición de elegibilidad
            if (sortedItems.size < 4) {
                return MotorResult.Empty
            }

            // 6.8.5 Selección del ítem correcto
            val candidates = buildCandidates(sortedItems, state.lastCorrectItemId)
            if (candidates.isEmpty()) {
                return MotorResult.Empty
            }

            val i = state.exerciseCounter % candidates.size
            val correctId = candidates[i]

            // 6.8.6 Selección de distractores
            val pool = sortedItems.filter { it != correctId }
            if (pool.size < 3) {
                return MotorResult.Empty
            }

            val startIndex = run {
                val idx = pool.indexOfFirst { it > correctId }
                if (idx == -1) 0 else idx
            }

            val d1 = pool[startIndex % pool.size]
            val d2 = pool[(startIndex + 1) % pool.size]
            val d3 = pool[(startIndex + 2) % pool.size]

            // Invariante: no duplicados
            val optionSet = linkedSetOf(correctId, d1, d2, d3)
            if (optionSet.size != 4) {
                // Si el dataset tuviera duplicados raros, el motor debe degradar a Empty (sin ejercicio válido)
                return MotorResult.Empty
            }

            // 6.8.7 Construcción y orden final de opciones
            val optionsBase = listOf(correctId, d1, d2, d3)
            val k = state.exerciseCounter % 4
            val optionsFinal = rotateLeft(optionsBase, k)

            // 6.8.8 Derivación de exerciseId
            val exerciseId = "$categoryId:${state.exerciseCounter}"

            val exercise = OfflineExercise(
                categoryId = categoryId,
                exerciseId = exerciseId,
                correctItemId = correctId,
                options = optionsFinal,
                correctOptionId = correctId,
            )

            // 6.8.9 Actualización de estado interno (solo en Success)
            state = MotorState(
                lastCorrectItemId = correctId,
                exerciseCounter = state.exerciseCounter + 1,
            )

            MotorResult.Success(exercise)
        } catch (t: Throwable) {
            MotorResult.Error(t)
        }
    }

    private fun buildCandidates(sortedItems: List<String>, lastCorrectItemId: String?): List<String> {
        return if (lastCorrectItemId != null && sortedItems.size > 1) {
            val filtered = sortedItems.filter { it != lastCorrectItemId }
            if (filtered.isEmpty()) sortedItems else filtered
        } else {
            sortedItems
        }
    }

    private fun rotateLeft(list: List<String>, k: Int): List<String> {
        if (list.isEmpty()) return list
        val shift = ((k % list.size) + list.size) % list.size
        if (shift == 0) return list
        return list.drop(shift) + list.take(shift)
    }
}
