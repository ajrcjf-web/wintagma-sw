package com.wintagma.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wintagma.android.data.local.dao.CategoryDao
import com.wintagma.android.data.local.dao.LexicalItemDao
import com.wintagma.android.data.local.entity.Category
import com.wintagma.android.data.local.entity.LexicalItem

@Database(
    entities = [
        Category::class,
        LexicalItem::class
    ],
    version = 1,
    exportSchema = true
)
abstract class WintagmaDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun lexicalItemDao(): LexicalItemDao
}
