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
 * Tests stock-change color presentation.
 *
 * Every Pager page:
 *
 * - positive change -> stock name red
 * - negative change -> stock name green
 * - zero / missing change -> stock name uses normal text color
 *
 * CHANGE value:
 *
 * - positive change -> change value red
 * - negative change -> change value green
 * - zero / missing change -> value keeps original presentation
 *
 * CHANGE_PERCENTAGE value:
 *
 * - percentage value itself does not carry the directional red/green
 *   presentation
 */
class StockPagerChangeColorTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun positiveChangeUsesRedValuePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "POSITIVE",
                    change =
                        BigDecimal(
                            "1.25"
                        ),
                ),
            page =
                changePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "POSITIVE 漲跌為正，漲跌數值紅字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun negativeChangeUsesGreenValuePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "NEGATIVE",
                    change =
                        BigDecimal(
                            "-1.25"
                        ),
                ),
            page =
                changePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "NEGATIVE 漲跌為負，漲跌數值綠字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun zeroChangeUsesOriginalValuePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "ZERO",
                    change =
                        BigDecimal.ZERO,
                ),
            page =
                changePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "ZERO 漲跌為中性，漲跌數值維持原色"
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingChangeUsesOriginalValuePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "MISSING",
                    change =
                        null,
                ),
            page =
                changePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "MISSING 漲跌為中性，漲跌數值維持原色"
            )
            .assertIsDisplayed()
    }

    @Test
    fun positiveChangePercentageUsesRedStockNamePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "PERCENT_POSITIVE",
                    change =
                        BigDecimal(
                            "1.25"
                        ),
                ),
            page =
                changePercentagePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "PERCENT_POSITIVE 漲跌為正，股票名稱紅字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun negativeChangePercentageUsesGreenStockNamePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "PERCENT_NEGATIVE",
                    change =
                        BigDecimal(
                            "-1.25"
                        ),
                ),
            page =
                changePercentagePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "PERCENT_NEGATIVE 漲跌為負，股票名稱綠字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun zeroChangePercentageUsesNormalStockNamePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "PERCENT_ZERO",
                    change =
                        BigDecimal.ZERO,
                ),
            page =
                changePercentagePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "PERCENT_ZERO 漲跌為中性，股票名稱一般色"
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingChangePercentageUsesNormalStockNamePresentation() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "PERCENT_MISSING",
                    change =
                        null,
                ),
            page =
                changePercentagePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "PERCENT_MISSING 漲跌為中性，股票名稱一般色"
            )
            .assertIsDisplayed()
    }

    /**
     * A normal Pager field verifies that stock-name direction colors are
     * not restricted to the CHANGE or CHANGE_PERCENTAGE pages.
     */
    @Test
    fun positiveChangeUsesRedStockNameOnRegularPagerPage() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "REGULAR_POSITIVE",
                    change =
                        BigDecimal(
                            "1.25"
                        ),
                ),
            page =
                closingPricePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "REGULAR_POSITIVE 漲跌為正，股票名稱紅字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun negativeChangeUsesGreenStockNameOnRegularPagerPage() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "REGULAR_NEGATIVE",
                    change =
                        BigDecimal(
                            "-1.25"
                        ),
                ),
            page =
                closingPricePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "REGULAR_NEGATIVE 漲跌為負，股票名稱綠字"
            )
            .assertIsDisplayed()
    }

    @Test
    fun zeroChangeUsesNormalStockNameOnRegularPagerPage() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "REGULAR_ZERO",
                    change =
                        BigDecimal.ZERO,
                ),
            page =
                closingPricePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "REGULAR_ZERO 漲跌為中性，股票名稱一般色"
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingChangeUsesNormalStockNameOnRegularPagerPage() {
        setPagerPage(
            stock =
                stock(
                    code =
                        "REGULAR_MISSING",
                    change =
                        null,
                ),
            page =
                closingPricePage(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "REGULAR_MISSING 漲跌為中性，股票名稱一般色"
            )
            .assertIsDisplayed()
    }

    /**
     * Renders one Pager page directly so color tests remain independent
     * of Pager navigation and persistent field configuration.
     */
    private fun setPagerPage(
        stock: Stock,
        page: StockPagerPage,
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                StockPagerList(
                    stocks =
                        listOf(
                            stock
                        ),
                    page =
                        page,
                    listState =
                        rememberLazyListState(),
                )
            }
        }
    }

    private fun changePage():
        StockPagerPage {

        return requireNotNull(
            stockPagerPages
                .firstOrNull { page ->
                    page.key ==
                        StockPagerFieldKey.CHANGE
                }
        )
    }

    private fun changePercentagePage():
        StockPagerPage {

        return requireNotNull(
            stockPagerPages
                .firstOrNull { page ->
                    page.key ==
                        StockPagerFieldKey
                            .CHANGE_PERCENTAGE
                }
        )
    }

    private fun closingPricePage():
        StockPagerPage {

        return requireNotNull(
            stockPagerPages
                .firstOrNull { page ->
                    page.key ==
                        StockPagerFieldKey
                            .CLOSING_PRICE
                }
        )
    }

    private fun stock(
        code: String,
        change: BigDecimal?,
    ): Stock {

        return Stock(
            Code =
                code,
            Name =
                "$code Name",
            day =
                StockDayItem(
                    Code =
                        code,
                    Name =
                        "$code Name",
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
