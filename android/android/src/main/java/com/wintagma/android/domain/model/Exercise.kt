package com.wintagma.android.domain.model

data class Exercise(
    val exercise_id: Int,
    val category_id: Int,
    val lexical_item_id: Int,
    val prompt: String,
    val options: List<ExerciseOption>
)
