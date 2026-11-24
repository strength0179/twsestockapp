package com.example.twsestockapp.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.twsestockapp.TWSEStockApplication

/**
 * Route-level dependency entry point for the main stock screen.
 *
 * This component resolves production dependencies, creates the default
 * ViewModel implementation, and delegates presentation to
 * [MainScreenScreen].
 */
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val application =
        LocalContext
            .current
            .applicationContext as
            TWSEStockApplication

    val container =
        application.container

    val viewModel:
        DefaultMainScreenViewModel =
        viewModel {
            DefaultMainScreenViewModel(
                dataRepository =
                    container.dataRepository,
                stockSelectionRepository =
                    container
                        .stockSelectionRepository,
                stockFieldSelectionRepository =
                    container
                        .stockFieldSelectionRepository,
            )
        }

    MainScreenScreen(
        viewModel =
            viewModel,
        onItemClick =
            onItemClick,
        modifier =
            modifier,
    )
}
