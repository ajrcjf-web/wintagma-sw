package com.wintagma.android.feature.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wintagma.android.core.model.Category
import com.wintagma.android.data.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategorySelectionViewModel(
    private val contentRepository: ContentRepository,
) : ViewModel() {

    var uiState: CategorySelectionUiState by mutableStateOf(CategorySelectionUiState())
        private set

    fun loadCategories() {
        viewModelScope.launch {
            // Estado inicial: cargando, sin error
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                // Llamada de red en hilo de IO
                val categories: List<Category> = withContext(Dispatchers.IO) {
                    contentRepository.getCategories()
                }

                // Éxito
                uiState = uiState.copy(
                    isLoading = false,
                    categories = categories,
                    error = null,
                )

            } catch (e: Exception) {
                e.printStackTrace()

                // Error controlado → no revienta la app
                uiState = uiState.copy(
                    isLoading = false,
                    categories = emptyList(),
                    error = e.message ?: "internal_error",
                )
            }
        }
    }
}
