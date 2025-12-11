package com.wintagma.android.feature.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wintagma.android.core.model.Category

/**
 * Entry point de la pantalla de selección de categoría.
 *
 * El ViewModel se recibe desde fuera (no usamos viewModel())
 * para no introducir dependencias nuevas. Aquí solo
 * disparamos loadCategories() automáticamente.
 */
@Composable
fun CategorySelectionScreen(
    viewModel: CategorySelectionViewModel,
    onCategorySelected: (Int) -> Unit,
) {
    // Lanzamos la carga AUTOMÁTICAMENTE al entrar en la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    CategorySelectionContent(
        uiState = viewModel.uiState,
        onCategorySelected = onCategorySelected,
    )
}

/**
 * Contenido puro de la pantalla: renderiza según el uiState.
 */
@Composable
fun CategorySelectionContent(
    uiState: CategorySelectionUiState,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> {
            LoadingState(modifier)
        }

        uiState.categories.isEmpty() -> {
            EmptyState(modifier)
        }

        else -> {
            CategoryListState(
                categories = uiState.categories,
                onCategorySelected = onCategorySelected,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No hay categorías disponibles",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun CategoryListState(
    categories: List<Category>,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        categories.forEach { category ->
            Button(
                onClick = { onCategorySelected(category.categoryId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text(text = category.name)
            }
        }
    }
}
