package com.example.twsestockapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lifecycle-aware base implementation of [MainScreenViewModel].
 *
 * Shared responsibilities:
 *
 * - convert the main-screen state Flow into a StateFlow
 * - use one consistent subscription policy
 * - expose Loading as the initial state
 * - launch repository commands in viewModelScope
 *
 * Concrete implementations remain responsible for routing commands to
 * their dependencies.
 */
abstract class BaseMainScreenViewModel(
    sourceUiState:
        Flow<MainScreenUiState>,
) : ViewModel(), MainScreenViewModel {

    final override val uiState:
        StateFlow<MainScreenUiState> =
        sourceUiState.stateIn(
            scope =
                viewModelScope,
            started =
                SharingStarted
                    .WhileSubscribed(
                        stopTimeoutMillis =
                            5000,
                    ),
            initialValue =
                MainScreenUiState.Loading,
        )

    /**
     * Executes one suspend command in the ViewModel lifecycle scope.
     */
    protected fun launchCommand(
        command:
            suspend () -> Unit,
    ) {
        viewModelScope.launch {
            command()
        }
    }
}
