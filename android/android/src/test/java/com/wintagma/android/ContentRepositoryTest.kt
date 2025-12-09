package com.wintagma.android.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepositoryTest {

    @Test
    fun `getCategories no devuelve lista vacia`() {
        val repository = ContentRepository()

        val categories = repository.getCategories()

        assertFalse(categories.isEmpty())
    }

    @Test
    fun `getCategories contiene la categoria Basico`() {
        val repository = ContentRepository()

        val categories = repository.getCategories()

        assertTrue(categories.any { it.name == "Básico" })
    }
}
