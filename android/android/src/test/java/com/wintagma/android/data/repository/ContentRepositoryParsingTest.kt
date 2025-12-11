package com.wintagma.android.data.repository

import com.wintagma.android.core.model.Category
import com.wintagma.android.core.model.LexicalItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepositoryParsingTest {

    @Test
    fun `parseCategoriesJson mapea correctamente el JSON de categorias`() {
        val json = """
            {
              "categories": [
                { "category_id": 1, "name": "Compras en supermercado" },
                { "category_id": 2, "name": "Reunión de trabajo" }
              ]
            }
        """.trimIndent()

        val categories: List<Category> = parseCategoriesJson(json)

        assertEquals(2, categories.size)
        val first = categories[0]
        val second = categories[1]

        assertEquals(1, first.categoryId)
        assertEquals("Compras en supermercado", first.name)

        assertEquals(2, second.categoryId)
        assertEquals("Reunión de trabajo", second.name)
    }

    @Test
    fun `parseLexicalItemsJson mapea correctamente el JSON de lexical items`() {
        val json = """
            {
              "items": [
                { "lexical_item_id": 101, "category_id": 1, "text": "bolt" },
                { "lexical_item_id": 102, "category_id": 1, "text": "nut" }
              ]
            }
        """.trimIndent()

        val items: List<LexicalItem> = parseLexicalItemsJson(json)

        assertEquals(2, items.size)

        val first = items[0]
        val second = items[1]

        assertEquals(101, first.lexicalItemId)
        assertEquals(1, first.categoryId)
        assertEquals("bolt", first.text)

        assertEquals(102, second.lexicalItemId)
        assertEquals(1, second.categoryId)
        assertEquals("nut", second.text)
    }

    @Test
    fun `parseCategoriesJson maneja cuerpo vacio devolviendo lista vacia`() {
        val categories = parseCategoriesJson("")
        assertTrue(categories.isEmpty())
    }

    @Test
    fun `parseLexicalItemsJson maneja cuerpo vacio devolviendo lista vacia`() {
        val items = parseLexicalItemsJson("")
        assertTrue(items.isEmpty())
    }
}
