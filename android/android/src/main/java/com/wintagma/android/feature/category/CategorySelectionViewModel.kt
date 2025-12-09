package com.wintagma.android.feature.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.core.model.Category

/**
 * ViewModel de selección de categoría.
 * Usa ContentRepository, pero en este MP la obtención real
 * de datos sigue siendo un TODO en el repositorio.
 */
class CategorySelectionViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {

    var uiState: CategorySelectionUiState by mutableStateOf(CategorySelectionUiState())
        private set

    fun loadCategories() {
        uiState = uiState.copy(isLoading = true)

        val categories = contentRepository.getCategories()

        uiState = CategorySelectionUiState(
            isLoading = false,
            categories = categories,
        )
    }
}
