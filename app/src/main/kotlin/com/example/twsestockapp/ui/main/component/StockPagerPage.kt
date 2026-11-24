package com.example.twsestockapp.ui.main.component

import androidx.compose.ui.graphics.Color
import com.example.twsestockapp.data.model.Stock
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Stable identifiers for stock pager fields.
 *
 * These values are persistence keys.
 *
 * They must remain stable even if a field's visible title changes.
 */
internal object StockPagerFieldKey {

    const val CLOSING_PRICE =
        "closingPrice"

    const val CHANGE =
        "change"

    const val CHANGE_PERCENTAGE =
        "changePercentage"

    const val TRADE_VOLUME =
        "tradeVolume"

    const val MONTHLY_AVERAGE_PRICE =
        "monthlyAveragePrice"

    const val PE_RATIO =
        "peRatio"

    const val DIVIDEND_YIELD =
        "dividendYield"

    const val PB_RATIO =
        "pbRatio"
}

/**
 * Describes one page shown by the stock pager.
 *
 * @property key Stable identifier used for persistence.
 * @property title User-visible title.
 * @property titleColor Color used by the page title.
 * @property valueText Converts one Stock object into displayed text.
 */
internal data class StockPagerPage(
    val key: String,
    val title: String,
    val titleColor: Color,
    val valueText: (Stock) -> String,
)

/**
 * All available stock pager pages.
 *
 * This collection represents the complete field catalog.
 *
 * User configuration will later determine which entries are displayed
 * and in what order.
 */
internal val stockPagerPages: List<StockPagerPage> =
    listOf(
        StockPagerPage(
            key =
                StockPagerFieldKey.CLOSING_PRICE,
            title = "收盤價",
            titleColor = Color(0xFF1565C0),
            valueText = { stock ->
                formatDecimal(
                    stock.day?.ClosingPrice
                        ?: stock.average?.ClosingPrice
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.CHANGE,
            title = "漲跌",
            titleColor = Color(0xFFD84315),
            valueText = { stock ->
                formatSignedDecimal(
                    stock.day?.Change
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.CHANGE_PERCENTAGE,
            title = "漲跌幅",
            titleColor = Color(0xFFC62828),
            valueText = { stock ->
                formatChangePercentage(
                    stock
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.TRADE_VOLUME,
            title = "成交量",
            titleColor = Color(0xFF2E7D32),
            valueText = { stock ->
                stock.day
                    ?.TradeVolume
                    ?.takeIf { value ->
                        value.isNotBlank()
                    }
                    ?: NO_DATA_TEXT
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.MONTHLY_AVERAGE_PRICE,
            title = "月均價",
            titleColor = Color(0xFF6A1B9A),
            valueText = { stock ->
                formatDecimal(
                    stock.average
                        ?.MonthlyAveragePrice
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.PE_RATIO,
            title = "本益比",
            titleColor = Color(0xFF00838F),
            valueText = { stock ->
                formatDecimal(
                    stock.valuation?.PEratio
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.DIVIDEND_YIELD,
            title = "殖利率",
            titleColor = Color(0xFFAD1457),
            valueText = { stock ->
                formatPercentage(
                    stock.valuation
                        ?.DividendYield
                )
            },
        ),
        StockPagerPage(
            key =
                StockPagerFieldKey.PB_RATIO,
            title = "股價淨值比",
            titleColor = Color(0xFF5D4037),
            valueText = { stock ->
                formatDecimal(
                    stock.valuation?.PBratio
                )
            },
        ),
    )

private const val NO_DATA_TEXT =
    "--"

private fun formatDecimal(
    value: BigDecimal?,
): String {
    return value
        ?.stripTrailingZeros()
        ?.toPlainString()
        ?: NO_DATA_TEXT
}

private fun formatSignedDecimal(
    value: BigDecimal?,
): String {
    if (value == null) {
        return NO_DATA_TEXT
    }

    val text =
        value
            .stripTrailingZeros()
            .toPlainString()

    return if (
        value > BigDecimal.ZERO
    ) {
        "+$text"
    } else {
        text
    }
}

private fun formatPercentage(
    value: BigDecimal?,
): String {
    if (value == null) {
        return NO_DATA_TEXT
    }

    return "${
        value
            .stripTrailingZeros()
            .toPlainString()
    }%"
}

/**
 * Calculates:
 *
 * previous close = current close - change
 * change percent = change / previous close * 100
 */
private fun formatChangePercentage(
    stock: Stock,
): String {
    val closingPrice =
        stock.day?.ClosingPrice
            ?: return NO_DATA_TEXT

    val change =
        stock.day.Change
            ?: return NO_DATA_TEXT

    val previousClosingPrice =
        closingPrice.subtract(
            change
        )

    if (
        previousClosingPrice.compareTo(
            BigDecimal.ZERO
        ) == 0
    ) {
        return NO_DATA_TEXT
    }

    val percentage =
        change
            .multiply(
                BigDecimal("100")
            )
            .divide(
                previousClosingPrice,
                2,
                RoundingMode.HALF_UP,
            )

    val percentageText =
        percentage
            .stripTrailingZeros()
            .toPlainString()

    return if (
        percentage > BigDecimal.ZERO
    ) {
        "+$percentageText%"
    } else {
        "$percentageText%"
    }
}
