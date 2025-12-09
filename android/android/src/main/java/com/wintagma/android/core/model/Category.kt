package com.wintagma.android.core.model

/**
 * Modelo de dominio Category para la app Android.
 *
 * Coincide con el modelo descrito en los MPs de APP:
 * - categoryId: identificador interno de la categoría.
 * - name: nombre visible de la categoría.
 */
data class Category(
    val categoryId: Int,
    val name: String
)
