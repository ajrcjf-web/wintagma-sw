package com.wintagma.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.wintagma.android.data.local.entity.LexicalItem

@Dao
interface LexicalItemDao {

    @Query("SELECT * FROM lexical_items WHERE categoryId = :categoryId ORDER BY id ASC")
    suspend fun getByCategoryIdOrdered(categoryId: String): List<LexicalItem>

    @Query("SELECT COUNT(*) FROM lexical_items WHERE categoryId = :categoryId")
    suspend fun countByCategoryId(categoryId: String): Int
}
