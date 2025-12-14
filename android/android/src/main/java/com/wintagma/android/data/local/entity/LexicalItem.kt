package com.wintagma.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lexical_items",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["categoryId"])
    ]
)
data class LexicalItem(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val termDe: String,
    val termEs: String,
)
