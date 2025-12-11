package com.wintagma.android.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

/**
 * Tests de integración que requieren backend corriendo.
 * Se marcan como @Ignore para tests unitarios.
 * Ejecutar manualmente cuando el backend esté disponible.
 */
class ContentRepositoryTest {

    @Ignore("Requiere backend corriendo en http://10.0.2.2:8000")
    @Test
    fun `getCategories no devuelve lista vacia`() {
        val repository = ContentRepository()

        val categories = repository.getCategories()

        assertFalse(categories.isEmpty())
    }

    @Ignore("Requiere backend corriendo en http://10.0.2.2:8000")
    @Test
    fun `getCategories contiene la categoria Basico`() {
        val repository = ContentRepository()

        val categories = repository.getCategories()

        assertTrue(categories.any { it.name == "Básico" })
    }
}
