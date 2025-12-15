package com.wintagma.android.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wintagma.android.data.local.db.WintagmaDatabase
import com.wintagma.android.data.local.seed.SeedInitializer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeedInitializerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val db: WintagmaDatabase = Room.inMemoryDatabaseBuilder(
        context,
        WintagmaDatabase::class.java
    ).build()

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun seed_is_loaded_into_room_from_assets() = runBlocking {
        val initializer = SeedInitializer(context, db)

        // BD inicialmente vacía
        val before = db.categoryDao().getAllOrdered()
        assertTrue(before.isEmpty())

        // Ejecutar seed
        initializer.initializeIfEmpty()

        // Verificar que hay categorías e ítems
        val categories = db.categoryDao().getAllOrdered()
        assertTrue("Seed must insert at least 5 categories", categories.size >= 5)

        // Verificar que al menos una categoría tiene ≥ 4 ítems
        val categoryId = categories.first().id
        val itemsCount = db.lexicalItemDao().countByCategoryId(categoryId)
        assertTrue("Category must have at least 4 lexical items", itemsCount >= 4)
    }
}
