package com.wintagma.android

import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigationStateTest {

    @Test
    fun `estado inicial empieza en seleccion de categoria y sin categoria seleccionada`() {
        val state = AppNavigationState()

        assertEquals(RootScreen.CategorySelection, state.currentScreen)
        assertEquals(null, state.selectedCategoryId)
    }

    @Test
    fun `onCategorySelected mueve a pantalla de ejercicio y guarda categoryId`() {
        val initial = AppNavigationState()

        val updated = initial.onCategorySelected(42)

        assertEquals(RootScreen.Exercise, updated.currentScreen)
        assertEquals(42, updated.selectedCategoryId)
    }
}
