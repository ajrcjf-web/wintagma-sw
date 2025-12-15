package com.wintagma.android.domain.offline.motor

import com.wintagma.android.domain.offline.model.OfflineExercise

sealed class MotorResult {
    data class Success(val exercise: OfflineExercise) : MotorResult()
    data object Empty : MotorResult()
    data class Error(val throwable: Throwable) : MotorResult()
}
