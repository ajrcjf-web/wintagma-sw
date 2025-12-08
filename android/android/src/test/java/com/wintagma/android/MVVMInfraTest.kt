package com.wintagma.android

import com.wintagma.android.core.*
import org.junit.Test

class MVVMInfraTest {

    private data class TestState(val value: Int): UiState
    private object TestEvent: UiEvent
    private object TestEffect: UiEffect

    private class TestViewModel: ViewModelBase<TestState, TestEvent, TestEffect>(
        initialState = TestState(0)
    ) {
        override fun onEvent(event: TestEvent) {
            // no-op
        }
    }

    @Test
    fun testViewModelBase_initialStateOK() {
        val vm = TestViewModel()
        assert(vm.state.value.value == 0)
    }
}
