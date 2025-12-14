package com.wintagma.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.wintagma.android.data.local.entity.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    suspend fun getAllOrdered(): List<Category>
}
