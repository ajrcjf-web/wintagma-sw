package com.wintagma.android.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wintagma.android.data.local.dao.CategoryDao
import com.wintagma.android.data.local.dao.LexicalItemDao
import com.wintagma.android.data.local.db.WintagmaDatabase
import com.wintagma.android.data.local.entity.Category
import com.wintagma.android.data.local.entity.LexicalItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomSchemaTest {

    private val db: WintagmaDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        WintagmaDatabase::class.java
    ).build()

    private val categoryDao: CategoryDao = db.categoryDao()
    private val lexicalItemDao: LexicalItemDao = db.lexicalItemDao()

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun createsDb_and_enforcesFkRestrict(): Unit = runBlocking {
        // Insert category
        categoryDaoInsert(Category(id = "cat_01", name = "Categoria 1", orderIndex = 0))

        // Insert lexical item with valid FK
        lexicalItemDaoInsert(
            LexicalItem(
                id = "li_01",
                categoryId = "cat_01",
                termDe = "Haus",
                termEs = "Casa",
            )
        )

        val count = lexicalItemDao.countByCategoryId("cat_01")
        assertEquals(1, count)

        // Insert lexical item with invalid FK -> must fail
        try {
            lexicalItemDaoInsert(
                LexicalItem(
                    id = "li_02",
                    categoryId = "cat_missing",
                    termDe = "Baum",
                    termEs = "Árbol",
                )
            )
            throw AssertionError("Expected FK constraint failure")
        } catch (e: Exception) {
            // Room wraps constraints; accept any exception indicating constraint violation
            // This is acceptable as long as it fails due to constraint in SQLite layer
        }
    }

    private suspend fun categoryDaoInsert(category: Category) {
        // No insert DAO in contract; use raw SQL via db for test-only insertion
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO categories(id, name, orderIndex) VALUES(?, ?, ?)",
            arrayOf(category.id, category.name, category.orderIndex)
        )
    }

    private suspend fun lexicalItemDaoInsert(item: LexicalItem) {
        db.openHelper.writableDatabase.execSQL(
            "INSERT INTO lexical_items(id, categoryId, termDe, termEs) VALUES(?, ?, ?, ?)",
            arrayOf(item.id, item.categoryId, item.termDe, item.termEs)
        )
    }
}
