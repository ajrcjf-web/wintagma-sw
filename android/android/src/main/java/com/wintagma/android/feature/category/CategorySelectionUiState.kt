package com.wintagma.android.feature.category

import com.wintagma.android.core.model.Category

data class CategorySelectionUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
)
