package com.example.twsestockapp.ui.main.component

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.remote.model.StockDayItem
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

/**
 * Tests the presentation semantics of price-change colors.
 *
 * Taiwan-market convention:
 *
 * - positive change -> red
 * - negative change -> green
 * - zero / missing change -> neutral
 */
class StockPagerChangeColorTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun positiveChangeUsesRedPresentation() {
        setChangePage(
            stock =
                stock(
                    code =
                        "POSITIVE",
                    change =
                        BigDecimal(
                            "1.25"
                        ),
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "POSITIVE 漲跌為正，紅字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun negativeChangeUsesGreenPresentation() {
        setChangePage(
            stock =
                stock(
                    code =
                        "NEGATIVE",
                    change =
                        BigDecimal(
                            "-1.25"
                        ),
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "NEGATIVE 漲跌為負，綠字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun zeroChangeUsesNeutralPresentation() {
        setChangePage(
            stock =
                stock(
                    code =
                        "ZERO",
                    change =
                        BigDecimal.ZERO,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "ZERO 漲跌為中性"
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingChangeUsesNeutralPresentation() {
        setChangePage(
            stock =
                stock(
                    code =
                        "MISSING",
                    change =
                        null,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "MISSING 漲跌為中性"
            )
            .assertIsDisplayed()
    }

    /**
     * Renders only the CHANGE page so this test remains independent of
     * Pager navigation and field-selection configuration.
     */
    private fun setChangePage(
        stock: Stock,
    ) {
        val changePage =
            requireNotNull(
                stockPagerPages
                    .firstOrNull { page ->
                        page.key ==
                            StockPagerFieldKey.CHANGE
                    }
            )

        composeTestRule.setContent {
            TWSEStockAppTheme {
                StockPagerList(
                    stocks =
                        listOf(
                            stock
                        ),
                    page =
                        changePage,
                    listState =
                        rememberLazyListState(),
                )
            }
        }
    }

    private fun stock(
        code: String,
        change: BigDecimal?,
    ): Stock {

        return Stock(
            Code =
                code,
            Name =
                code,
            day =
                StockDayItem(
                    Code =
                        code,
                    Name =
                        code,
                    TradeVolume =
                        "1000",
                    TradeValue =
                        "100000",
                    OpeningPrice =
                        BigDecimal(
                            "100"
                        ),
                    HighestPrice =
                        BigDecimal(
                            "105"
                        ),
                    LowestPrice =
                        BigDecimal(
                            "95"
                        ),
                    ClosingPrice =
                        BigDecimal(
                            "102"
                        ),
                    Change =
                        change,
                    Transaction =
                        "100",
                ),
        )
    }
}
