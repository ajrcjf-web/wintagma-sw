package com.wintagma.android.feature.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wintagma.android.core.model.Category

/**
 * Pantalla de selección de categoría.
 *
 * Flujo oficial ET v1.4:
 *   categoría → ejercicio → respuesta → feedback
 *
 * Navigation aún no se implementa: se delega a onCategorySelected.
 */
@Composable
fun CategorySelectionScreen(
    viewModel: CategorySelectionViewModel,
    onCategorySelected: (Int) -> Unit,
) {
    // Cargamos categorías al entrar a la pantalla.
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    CategorySelectionContent(
        uiState = viewModel.uiState,
        onCategorySelected = onCategorySelected,
    )
}

/**
 * Contenido puro de la pantalla, independiente del ViewModel.
 * Facilita pruebas y reutilización.
 */
@Composable
fun CategorySelectionContent(
    uiState: CategorySelectionUiState,
    onCategorySelected: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> {
                LoadingState()
            }

            uiState.categories.isEmpty() -> {
                EmptyState()
            }

            else -> {
                CategoryList(
                    categories = uiState.categories,
                    onCategorySelected = onCategorySelected,
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = "Cargando categorías…",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No hay categorías disponibles.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun CategoryList(
    categories: List<Category>,
    onCategorySelected: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                onClick = { onCategorySelected(category.categoryId) },
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectionContentPreview() {
    val sampleState = CategorySelectionUiState(
        isLoading = false,
        categories = listOf(
            Category(categoryId = 1, name = "Compras en supermercado"),
            Category(categoryId = 2, name = "Reunión de trabajo"),
        ),
    )

    CategorySelectionContent(
        uiState = sampleState,
        onCategorySelected = {},
    )
}
