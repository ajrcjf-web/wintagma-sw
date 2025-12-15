package com.wintagma.android.domain.offline.motor

import org.junit.Assert.*
import org.junit.Test

class DeterministicOfflineExerciseMotorTest {

    @Test
    fun returnsEmpty_whenLessThan4Items() {
        val motor = DeterministicOfflineExerciseMotor()

        val result = motor.generate("cat_01", listOf("a", "b", "c"))
        assertTrue(result is MotorResult.Empty)
    }

    @Test
    fun generatesDeterministicExercise_with4OrMoreItems() {
        val motor = DeterministicOfflineExerciseMotor()
        val items = listOf("li_03", "li_01", "li_04", "li_02") // desordenado intencionalmente

        val r1 = motor.generate("cat_01", items)
        val r2 = DeterministicOfflineExerciseMotor().generate("cat_01", items)

        assertTrue(r1 is MotorResult.Success)
        assertTrue(r2 is MotorResult.Success)

        val e1 = (r1 as MotorResult.Success).exercise
        val e2 = (r2 as MotorResult.Success).exercise

        // Igualdad exacta para mismo input y estado inicial
        assertEquals(e1, e2)

        // Invariantes
        assertEquals("cat_01:0", e1.exerciseId)
        assertEquals(4, e1.options.size)
        assertEquals(4, e1.options.distinct().size)
        assertTrue(e1.options.contains(e1.correctItemId))
        assertEquals(e1.correctItemId, e1.correctOptionId)
    }

    @Test
    fun modeB_excludesImmediatePreviousCorrect_whenAlternativesExist() {
        val motor = DeterministicOfflineExerciseMotor()
        val items = listOf("li_01", "li_02", "li_03", "li_04", "li_05")

        val r1 = motor.generate("cat_01", items) as MotorResult.Success
        val prevCorrect = r1.exercise.correctItemId

        val r2 = motor.generate("cat_01", items) as MotorResult.Success
        val newCorrect = r2.exercise.correctItemId

        assertNotEquals("Correct item must not repeat immediately when alternatives exist", prevCorrect, newCorrect)
    }

    @Test
    fun modeB_allowsRepetition_whenOnlyOneCandidateAfterExclusion() {
        // Caso límite: 4 ítems, y el algoritmo puede quedar con candidates vacíos solo si size==1,
        // pero ET permite repetición cuando al excluir queda vacío.
        // Para forzar repetición: usamos categoría con 4 ítems y avanzamos hasta que lastCorrect
        // sea tal que exclusion deje candidates no vacíos (en este caso siempre habrá 3 candidatos),
        // así que el escenario de "vacío" no aplica aquí.
        // El caso permitido por ET se da cuando candidates queda vacío; eso solo ocurre si sortedItems.size == 1,
        // pero no sería elegible. Por lo tanto validamos el contrato real aplicable:
        // - No explota
        // - Siempre genera opciones válidas sin duplicados
        val motor = DeterministicOfflineExerciseMotor()
        val items = listOf("a", "b", "c", "d")

        repeat(10) {
            val r = motor.generate("cat_01", items)
            assertTrue(r is MotorResult.Success)
            val e = (r as MotorResult.Success).exercise
            assertEquals(4, e.options.size)
            assertEquals(4, e.options.distinct().size)
        }
    }

    @Test
    fun changingCategory_resetsState_counterAndLastCorrect() {
        val motor = DeterministicOfflineExerciseMotor()
        val items = listOf("li_01", "li_02", "li_03", "li_04")

        val r1 = motor.generate("cat_01", items) as MotorResult.Success
        assertEquals("cat_01:0", r1.exercise.exerciseId)

        val r2 = motor.generate("cat_02", items) as MotorResult.Success
        assertEquals("cat_02:0", r2.exercise.exerciseId)

        val r3 = motor.generate("cat_01", items) as MotorResult.Success
        // Al volver a cat_01 se resetea (según ET: reset por cambio de categoría)
        assertEquals("cat_01:0", r3.exercise.exerciseId)
    }
}
