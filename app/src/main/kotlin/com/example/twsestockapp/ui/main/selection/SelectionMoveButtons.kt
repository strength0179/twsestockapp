package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Shared movement controls used by ordered-selection editors.
 *
 * Both stock editing and Pager-field editing use this component so the
 * visual style and enabled/disabled behavior remain consistent.
 *
 * "+" moves an item toward the beginning.
 * "-" moves an item toward the end.
 */
@Composable
internal fun SelectionMoveButtons(
    forwardDescription: String,
    backwardDescription: String,
    canMoveForward: Boolean,
    canMoveBackward: Boolean,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                6.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        SelectionMoveButton(
            text =
                "+",
            contentDescription =
                forwardDescription,
            enabled =
                canMoveForward,
            onClick =
                onMoveForward,
        )

        SelectionMoveButton(
            text =
                "-",
            contentDescription =
                backwardDescription,
            enabled =
                canMoveBackward,
            onClick =
                onMoveBackward,
        )
    }
}

@Composable
private fun SelectionMoveButton(
    text: String,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick =
            onClick,
        enabled =
            enabled,
        modifier =
            modifier
                .size(40.dp)
                .semantics {
                    this.contentDescription =
                        contentDescription
                },
        shape =
            CircleShape,
        colors =
            IconButtonDefaults
                .filledTonalIconButtonColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .secondaryContainer,
                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .onSecondaryContainer,
                ),
    ) {
        Text(
            text =
                text,
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )
    }
}
