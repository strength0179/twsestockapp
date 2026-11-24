package com.example.twsestockapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Loading presentation for the main screen.
 */
@Composable
internal fun MainScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Error presentation for the main screen.
 */
@Composable
internal fun MainScreenError(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center,
    ) {
        Text(
            text =
                "Error loading data: " +
                    throwable.message
        )
    }
}
