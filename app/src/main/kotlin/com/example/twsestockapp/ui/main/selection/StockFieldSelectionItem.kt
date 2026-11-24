package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
 * Displays one Pager field inside the field-selection editor.
 *
 * Visible fields:
 * - show presentation order 1, 2, 3...
 * - expose shared movement controls
 * - expose a circular hide button
 *
 * Hidden fields:
 * - show x instead of a numeric order
 * - clicking x requests restoration
 * - movement controls remain visible but disabled
 *
 * Persistent absolute ordering is not calculated here.
 */
@Composable
internal fun StockFieldSelectionItem(
    state: StockFieldSelectionItemState,
    onShow: () -> Unit,
    onHide: () -> Unit,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        StockFieldVisibilityOrderBox(
            visibleDisplayOrder =
                state.visibleDisplayOrder,
            isVisible =
                state.isVisible,
            canShow =
                state.canShow,
            pageTitle =
                state.page.title,
            onShow =
                onShow,
        )

        Spacer(
            modifier =
                Modifier.width(12.dp),
        )

        Text(
            text =
                state.page.title,
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
            color =
                if (state.isVisible) {
                    state.page.titleColor
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                },
        )

        SelectionMoveButtons(
            forwardDescription =
                "將 ${state.page.title} 往前移",
            backwardDescription =
                "將 ${state.page.title} 往後移",
            canMoveForward =
                state.canMoveForward,
            canMoveBackward =
                state.canMoveBackward,
            onMoveForward =
                onMoveForward,
            onMoveBackward =
                onMoveBackward,
        )

        Spacer(
            modifier =
                Modifier.width(6.dp),
        )

        if (state.isVisible) {
            StockFieldHideButton(
                pageTitle =
                    state.page.title,
                enabled =
                    state.canHide,
                onClick =
                    onHide,
            )
        } else {
            /*
             * Preserve the same trailing width as visible rows so
             * movement controls stay horizontally aligned.
             */
            Spacer(
                modifier =
                    Modifier.size(40.dp),
            )
        }
    }
}

/**
 * Displays visible presentation order or hidden x state.
 *
 * Visible boxes are informational.
 * Hidden boxes are clickable and request show.
 */
@Composable
private fun StockFieldVisibilityOrderBox(
    visibleDisplayOrder: Int?,
    isVisible: Boolean,
    canShow: Boolean,
    pageTitle: String,
    onShow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description =
        if (isVisible) {
            "欄位顯示排序 $visibleDisplayOrder"
        } else {
            "顯示 $pageTitle"
        }

    Box(
        modifier =
            modifier
                .size(44.dp)
                .border(
                    width = 1.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .outline,
                    shape =
                        MaterialTheme
                            .shapes
                            .small,
                )
                .clickable(
                    enabled =
                        !isVisible &&
                            canShow,
                    onClick =
                        onShow,
                )
                .semantics {
                    contentDescription =
                        description
                },
        contentAlignment =
            Alignment.Center,
    ) {
        Text(
            text =
                if (isVisible) {
                    visibleDisplayOrder
                        ?.toString()
                        ?: ""
                } else {
                    "x"
                },
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            color =
                if (isVisible) {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                },
        )
    }
}

/**
 * Circular action used to hide a visible Pager field.
 *
 * The final remaining visible field keeps this control disabled.
 */
@Composable
private fun StockFieldHideButton(
    pageTitle: String,
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
                    contentDescription =
                        if (enabled) {
                            "隱藏 $pageTitle"
                        } else {
                            "$pageTitle 是最後一個顯示欄位"
                        }
                },
        shape =
            CircleShape,
        colors =
            IconButtonDefaults
                .filledTonalIconButtonColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .errorContainer,
                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .onErrorContainer,
                ),
    ) {
        Text(
            text = "×",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )
    }
}
