package com.wintagma.android.feature.category

import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.core.model.Category
import com.wintagma.android.core.model.LexicalItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeContentRepository : ContentRepository() {

    override fun getCategories(): List<Category> {
        return listOf(
            Category(categoryId = 1, name = "Compras en supermercado"),
            Category(categoryId = 2, name = "Reunión de trabajo"),
        )
    }

    override fun getLexicalItems(categoryId: Int): List<LexicalItem> {
        return emptyList()
    }
}

class CategorySelectionViewModelTest {

    @Test
    fun `loadCategories updates uiState with categories`() {
        val repository = FakeContentRepository()
        val viewModel = CategorySelectionViewModel(repository)

        // Estado inicial
        assertTrue(viewModel.uiState.categories.isEmpty())
        assertFalse(viewModel.uiState.isLoading)

        // Ejecutamos la carga
        viewModel.loadCategories()

        // Validamos el resultado
        val state = viewModel.uiState
        assertFalse(state.isLoading)
        assertEquals(2, state.categories.size)
        assertEquals("Compras en supermercado", state.categories[0].name)
        assertEquals(1, state.categories[0].categoryId)
    }
}
