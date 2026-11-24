package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock

/**
 * Renders one clickable stock row inside a Pager page.
 *
 * The row owns layout and interaction only.
 *
 * Field-specific colors and semantic descriptions arrive through
 * [StockPagerRowPresentation].
 */
@Composable
internal fun StockPagerRow(
    stock: Stock,
    value: String,
    presentation: StockPagerRowPresentation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick,
                )
                .semantics {
                    contentDescription =
                        "開啟 ${stock.Code} 股票估值"
                },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp,
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Text(
                    text =
                        stock.Code,
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )

                Text(
                    text =
                        stock.Name,
                    modifier =
                        Modifier.then(
                            if (
                                presentation
                                    .stockNameColorDescription !=
                                null
                            ) {
                                Modifier.semantics {
                                    contentDescription =
                                        presentation
                                            .stockNameColorDescription
                                }
                            } else {
                                Modifier
                            }
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    color =
                        presentation
                            .stockNameColor,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis,
                )
            }

            Text(
                text =
                    value,
                modifier =
                    Modifier
                        .weight(0.8f)
                        .then(
                            if (
                                presentation
                                    .valueColorDescription !=
                                null
                            ) {
                                Modifier.semantics {
                                    contentDescription =
                                        presentation
                                            .valueColorDescription
                                }
                            } else {
                                Modifier
                            }
                        ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                color =
                    presentation
                        .valueColor,
                fontWeight =
                    FontWeight.SemiBold,
                textAlign =
                    TextAlign.End,
                maxLines =
                    1,
            )
        }

        HorizontalDivider(
            color =
                MaterialTheme
                    .colorScheme
                    .outlineVariant,
        )
    }
}
