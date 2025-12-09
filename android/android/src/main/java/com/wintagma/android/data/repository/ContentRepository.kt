package com.wintagma.android.data.repository

import com.wintagma.android.core.model.Category
import com.wintagma.android.core.model.LexicalItem

/**
 * Repositorio de contenido alineado con la API del backend:
 *
 * - GET /content/categories
 * - GET /content/items/{category_id}
 *
 * Implementación actual:
 * - Devuelve datos locales (dummy) para permitir validar la UX sin backend real.
 * - La lógica HTTP real se implementará en MPs posteriores (INT-01).
 */
open class ContentRepository {

    /**
     * Equivalente a GET /content/categories.
     *
     * En esta fase devuelve categorías locales dummy.
     */
    open fun getCategories(): List<Category> {
        return listOf(
            Category(
                categoryId = 1,
                name = "Básico"
            ),
            Category(
                categoryId = 2,
                name = "Avanzado"
            ),
            Category(
                categoryId = 3,
                name = "Profesiones"
            )
        )
    }

    /**
     * Equivalente a GET /content/items/{category_id}.
     *
     * Sigue devolviendo lista vacía en modo mock.
     * MPs posteriores conectarán esto con el backend real.
     */
    open fun getLexicalItems(categoryId: Int): List<LexicalItem> {
        return emptyList()
    }
}
