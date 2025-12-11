package com.wintagma.android.domain.model

import com.wintagma.android.core.model.Category
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryDomainModelTest {

    @Test
    fun `Category expone correctamente categoryId y name`() {
        val category = Category(
            categoryId = 10,
            name = "Test"
        )

        assertEquals(10, category.categoryId)
        assertEquals("Test", category.name)
    }
}
