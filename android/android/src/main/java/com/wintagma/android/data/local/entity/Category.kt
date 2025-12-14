package com.wintagma.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["orderIndex"])
    ]
)
data class Category(
    @PrimaryKey
    val id: String,
    val name: String,
    val orderIndex: Int,
)
