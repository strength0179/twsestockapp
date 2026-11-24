package com.example.twsestockapp.ui.main.component

import androidx.compose.ui.graphics.Color
import com.example.twsestockapp.data.model.Stock

/**
 * Complete presentation values required by one StockPagerRow.
 *
 * Keeping these decisions outside the Composable prevents Pager-field
 * rules from leaking into row layout code.
 */
internal data class StockPagerRowPresentation(
    val valueColor: Color,
    val stockNameColor: Color,
    val valueColorDescription: String?,
    val stockNameColorDescription: String?,
)

/**
 * Resolves field-specific row presentation.
 *
 * Stock name:
 *
 * - every Pager page uses the stock's daily Change
 * - positive change -> stock name red
 * - negative change -> stock name green
 * - zero / missing change -> stock name uses normal text color
 *
 * CHANGE value:
 *
 * - positive -> change value red
 * - negative -> change value green
 * - neutral -> original page value color
 *
 * CHANGE_PERCENTAGE value:
 *
 * - percentage value uses normal text color
 *
 * Other field values:
 *
 * - value uses page.titleColor
 */
internal fun resolveStockPagerRowPresentation(
    stock: Stock,
    page: StockPagerPage,
    neutralTextColor: Color,
): StockPagerRowPresentation {

    val changeDirection =
        resolveStockChangeDirection(
            value =
                stock.day?.Change,
        )

    return StockPagerRowPresentation(
        valueColor =
            resolveValueColor(
                page =
                    page,
                changeDirection =
                    changeDirection,
                neutralTextColor =
                    neutralTextColor,
            ),
        stockNameColor =
            resolveStockNameColor(
                changeDirection =
                    changeDirection,
                neutralTextColor =
                    neutralTextColor,
            ),
        valueColorDescription =
            resolveValueColorDescription(
                stock =
                    stock,
                page =
                    page,
                changeDirection =
                    changeDirection,
            ),
        stockNameColorDescription =
            resolveStockNameColorDescription(
                stock =
                    stock,
                changeDirection =
                    changeDirection,
            ),
    )
}

/**
 * Resolves the value color for the current Pager page.
 */
private fun resolveValueColor(
    page: StockPagerPage,
    changeDirection: StockChangeDirection,
    neutralTextColor: Color,
): Color {

    return when (page.key) {
        StockPagerFieldKey.CHANGE -> {
            when (changeDirection) {
                StockChangeDirection.Positive ->
                    StockValueColors.Positive

                StockChangeDirection.Negative ->
                    StockValueColors.Negative

                StockChangeDirection.Neutral ->
                    page.titleColor
            }
        }

        StockPagerFieldKey.CHANGE_PERCENTAGE ->
            neutralTextColor

        else ->
            page.titleColor
    }
}

/**
 * Resolves the stock-name color.
 *
 * Every Pager page uses the same stock-change direction.
 */
private fun resolveStockNameColor(
    changeDirection: StockChangeDirection,
    neutralTextColor: Color,
): Color {

    return when (changeDirection) {
        StockChangeDirection.Positive ->
            StockValueColors.Positive

        StockChangeDirection.Negative ->
            StockValueColors.Negative

        StockChangeDirection.Neutral ->
            neutralTextColor
    }
}

/**
 * Exposes semantic color state for the CHANGE value.
 *
 * Non-CHANGE pages do not expose change-value color semantics.
 */
private fun resolveValueColorDescription(
    stock: Stock,
    page: StockPagerPage,
    changeDirection: StockChangeDirection,
): String? {

    if (
        page.key !=
        StockPagerFieldKey.CHANGE
    ) {
        return null
    }

    return when (changeDirection) {
        StockChangeDirection.Positive ->
            "${stock.Code} 漲跌為正，漲跌數值紅字"

        StockChangeDirection.Negative ->
            "${stock.Code} 漲跌為負，漲跌數值綠字"

        StockChangeDirection.Neutral ->
            "${stock.Code} 漲跌為中性，漲跌數值維持原色"
    }
}

/**
 * Exposes the stock-name color semantics used by every Pager page.
 */
private fun resolveStockNameColorDescription(
    stock: Stock,
    changeDirection: StockChangeDirection,
): String {

    return when (changeDirection) {
        StockChangeDirection.Positive ->
            "${stock.Code} 漲跌為正，股票名稱紅字"

        StockChangeDirection.Negative ->
            "${stock.Code} 漲跌為負，股票名稱綠字"

        StockChangeDirection.Neutral ->
            "${stock.Code} 漲跌為中性，股票名稱一般色"
    }
}
