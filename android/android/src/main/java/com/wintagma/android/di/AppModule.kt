package com.wintagma.android.di

import com.wintagma.android.data.repository.ContentRepository
import com.wintagma.android.data.repository.ExerciseRepository

object AppModule {

    val contentRepository: ContentRepository by lazy {
        ContentRepository()
    }

    val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepository()
    }
}
