package com.example.twsestockapp.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces Dispatchers.Main with a controllable TestDispatcher.
 *
 * ViewModels use viewModelScope, which dispatches work through
 * Dispatchers.Main. JVM unit tests therefore need a test Main
 * dispatcher so runTest can execute and control ViewModel coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher =
        StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(
        description: Description
    ) {
        Dispatchers.setMain(
            testDispatcher
        )
    }

    override fun finished(
        description: Description
    ) {
        Dispatchers.resetMain()
    }
}
