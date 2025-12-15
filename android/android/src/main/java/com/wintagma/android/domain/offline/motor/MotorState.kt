package com.wintagma.android.domain.offline.motor

data class MotorState(
    val lastCorrectItemId: String?,
    val exerciseCounter: Int,
) {
    companion object {
        fun initial(): MotorState = MotorState(
            lastCorrectItemId = null,
            exerciseCounter = 0,
        )
    }
}
