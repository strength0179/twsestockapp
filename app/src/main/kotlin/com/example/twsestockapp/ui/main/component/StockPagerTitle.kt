package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Displays the title row for the currently visible stock Pager page.
 *
 * Responsibilities:
 *
 * - display the fixed stock-column title
 * - display the current Pager field title
 * - apply the field-specific title color
 *
 * Pager state and navigation are intentionally kept outside this
 * presentation component.
 */
@Composable
internal fun StockPagerTitle(
    page: StockPagerPage,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme
                    .colorScheme
                    .surfaceContainer,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Text(
            text = "股票",
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface,
            fontWeight =
                FontWeight.Bold,
        )

        Text(
            text = page.title,
            modifier =
                Modifier.weight(0.8f),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            color =
                page.titleColor,
            fontWeight =
                FontWeight.Bold,
            textAlign =
                TextAlign.End,
        )
    }
}
