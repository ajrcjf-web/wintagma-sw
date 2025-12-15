package com.wintagma.android.data.local.seed

import android.content.Context
import androidx.room.withTransaction
import com.wintagma.android.data.local.dao.CategoryDao
import com.wintagma.android.data.local.dao.LexicalItemDao
import com.wintagma.android.data.local.db.WintagmaDatabase
import com.wintagma.android.data.local.entity.Category
import com.wintagma.android.data.local.entity.LexicalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SeedInitializer(
    private val context: Context,
    private val database: WintagmaDatabase,
) {

    private val categoryDao: CategoryDao = database.categoryDao()
    private val lexicalItemDao: LexicalItemDao = database.lexicalItemDao()

    /**
     * Inicializa la BD desde el seed JSON si no hay categorías.
     * Idempotente: si ya hay datos, no hace nada.
     */
    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val existing = categoryDao.getAllOrdered()
        if (existing.isNotEmpty()) {
            return@withContext
        }

        val (categories, lexicalItems) = loadAndValidateSeed()

        database.withTransaction {
            // Insert en orden: primero categorías, luego ítems (FK)
            // Si algo está mal, Room/SQLite lanzará excepción -> fail-fast
            categoryDao.insertAll(categories)
            lexicalItemDao.insertAll(lexicalItems)
        }
    }

    private fun loadAndValidateSeed(): Pair<List<Category>, List<LexicalItem>> {
        val jsonString = context.assets
            .open("seed/seed_v2_1.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonString)

        val version = root.getString("version")
        if (version != "v2.1") {
            throw IllegalStateException("Invalid seed version: $version")
        }

        val categoriesJson = root.getJSONArray("categories")
        val lexicalItemsJson = root.getJSONArray("lexicalItems")

        val categories = mutableListOf<Category>()
        val categoryIds = mutableSetOf<String>()

        for (i in 0 until categoriesJson.length()) {
            val obj = categoriesJson.getJSONObject(i)
            val id = obj.getString("id").trim()
            val name = obj.getString("name").trim()
            val orderIndex = obj.getInt("orderIndex")

            if (id.isEmpty() || name.isEmpty()) {
                throw IllegalStateException("Category with empty id or name in seed")
            }
            if (!categoryIds.add(id)) {
                throw IllegalStateException("Duplicated category id in seed: $id")
            }

            categories.add(
                Category(
                    id = id,
                    name = name,
                    orderIndex = orderIndex,
                )
            )
        }

        if (categories.size < 5) {
            throw IllegalStateException("Seed must contain at least 5 categories")
        }

        val items = mutableListOf<LexicalItem>()
        val itemIds = mutableSetOf<String>()

        for (i in 0 until lexicalItemsJson.length()) {
            val obj = lexicalItemsJson.getJSONObject(i)
            val id = obj.getString("id").trim()
            val categoryId = obj.getString("categoryId").trim()
            val termDe = obj.getString("termDe").trim()
            val termEs = obj.getString("termEs").trim()

            if (id.isEmpty() || categoryId.isEmpty() || termDe.isEmpty() || termEs.isEmpty()) {
                throw IllegalStateException("LexicalItem with empty required field in seed")
            }
            if (!itemIds.add(id)) {
                throw IllegalStateException("Duplicated lexical item id in seed: $id")
            }
            if (!categoryIds.contains(categoryId)) {
                throw IllegalStateException("LexicalItem with unknown categoryId: $categoryId")
            }

            items.add(
                LexicalItem(
                    id = id,
                    categoryId = categoryId,
                    termDe = termDe,
                    termEs = termEs,
                )
            )
        }

        return categories to items
    }
}
