package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Displays the current position within the selected stock Pager fields.
 *
 * The indicator exposes one stable semantics description:
 *
 * Pager 頁面 1 / 3
 *
 * This allows accessibility services and Compose UI tests to identify
 * the current page without depending on dot color or geometry.
 */
@Composable
internal fun StockPagerIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) {
        return
    }

    val safeCurrentPage =
        currentPage.coerceIn(
            minimumValue = 0,
            maximumValue =
                pageCount - 1,
        )

    Row(
        modifier = modifier
            .semantics {
                contentDescription =
                    "Pager 頁面 " +
                        "${safeCurrentPage + 1} / " +
                        pageCount
            }
            .padding(
                vertical = 8.dp,
            ),
        horizontalArrangement =
            Arrangement.spacedBy(
                6.dp
            ),
    ) {
        repeat(pageCount) { pageIndex ->
            val color =
                if (
                    pageIndex ==
                    safeCurrentPage
                ) {
                    MaterialTheme
                        .colorScheme
                        .primary
                } else {
                    MaterialTheme
                        .colorScheme
                        .outlineVariant
                }

            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .background(
                            color =
                                color,
                            shape =
                                CircleShape,
                        ),
            )
        }
    }
}
