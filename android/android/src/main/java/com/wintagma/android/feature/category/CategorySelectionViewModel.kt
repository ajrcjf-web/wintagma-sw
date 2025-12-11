package com.wintagma.android.feature.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.core.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel de selección de categoría.
 * Ahora realiza la llamada al repositorio en un hilo de IO
 * para evitar NetworkOnMainThreadException.
 */
class CategorySelectionViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {

    var uiState: CategorySelectionUiState by mutableStateOf(CategorySelectionUiState())
        private set

    fun loadCategories() {
        // Cambiamos a corrutina para no bloquear el hilo principal
        viewModelScope.launch {
            // 1) Estado de carga
            uiState = uiState.copy(isLoading = true)

            // 2) Ejecutar getCategories() en hilo de IO
            val categories: List<Category> = withContext(Dispatchers.IO) {
                contentRepository.getCategories()
            }

            // 3) Volver al hilo principal y actualizar estado
            uiState = CategorySelectionUiState(
                isLoading = false,
                categories = categories,
            )
        }
    }
}
