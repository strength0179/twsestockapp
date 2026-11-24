package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock
import java.math.BigDecimal

/**
 * Displays valuation details for one stock.
 *
 * The dialog intentionally exposes only valuation information:
 *
 * - PE ratio
 * - dividend yield percentage
 * - PB ratio
 *
 * Missing values are displayed as "--".
 */
@Composable
internal fun StockValuationDialog(
    stock: Stock,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier =
            modifier.semantics {
                contentDescription =
                    "股票估值 ${stock.Code}"
            },
        onDismissRequest =
            onDismissRequest,
        title = {
            Column {
                Text(
                    text =
                        stock.Name,
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                )

                Text(
                    text =
                        stock.Code,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )
            }
        },
        text = {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    ),
            ) {
                StockValuationRow(
                    label =
                        "本益比",
                    value =
                        formatValuationValue(
                            stock
                                .valuation
                                ?.PEratio
                        ),
                )

                StockValuationRow(
                    label =
                        "殖利率(%)",
                    value =
                        formatValuationValue(
                            stock
                                .valuation
                                ?.DividendYield
                        ),
                )

                StockValuationRow(
                    label =
                        "股價淨值比",
                    value =
                        formatValuationValue(
                            stock
                                .valuation
                                ?.PBratio
                        ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick =
                    onDismissRequest,
                modifier =
                    Modifier.semantics {
                        contentDescription =
                            "關閉股票估值"
                    },
            ) {
                Text(
                    text =
                        "關閉",
                )
            }
        },
    )
}

/**
 * One label/value pair inside the valuation dialog.
 */
@Composable
private fun StockValuationRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Text(
            text =
                label,
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
        )

        Text(
            text =
                value,
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
        )
    }
}

/**
 * Formats one valuation value without unnecessary trailing zeros.
 */
private fun formatValuationValue(
    value: BigDecimal?,
): String {

    return value
        ?.stripTrailingZeros()
        ?.toPlainString()
        ?: NO_VALUATION_DATA_TEXT
}

private const val NO_VALUATION_DATA_TEXT =
    "--"
