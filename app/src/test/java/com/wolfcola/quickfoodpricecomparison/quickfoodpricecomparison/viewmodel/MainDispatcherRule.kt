package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Points Dispatchers.Main at a test dispatcher for the duration of a test, so
 * MainViewModel's viewModelScope (backed by Dispatchers.Main.immediate) works in a plain
 * JUnit test without needing Robolectric.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
