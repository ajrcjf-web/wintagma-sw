package com.wintagma.android.core.model

/**
 * Representa un lexical_item del backend:
 * - lexical_item_id
 * - category_id
 * - text
 *
 * La app puede usarlo en flujos futuros si necesita mostrar listado
 * de items por categoría (/content/items/{category_id}).
 */
data class LexicalItem(
    val lexicalItemId: Int,
    val categoryId: Int,
    val text: String,
)
