package com.example.twsestockapp.ui.main.component

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

/**
 * Tests StockPagerList -> StockValuationDialog interaction.
 *
 * Dialog rendering details are tested separately by
 * StockValuationDialogTest.
 *
 * These tests intentionally avoid requiring stock-name or valuation
 * texts to be unique because the Pager row remains composed behind the
 * AlertDialog.
 */
class StockPagerValuationInteractionTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clickingStockRowOpensValuationDialog() {
        setPagerListContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "開啟 2330 股票估值"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertIsDisplayed()
    }

    @Test
    fun openedDialogDisplaysClickedStockIdentity() {
        setPagerListContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                ),
        )

        openDialog(
            code =
                "2330",
        )

        /*
         * The dialog semantic identifies the selected stock
         * unambiguously.
         *
         * The stock name also remains visible in the Pager row behind
         * the dialog, so we intentionally allow more than one node.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                "台積電"
            )
            .assertCountEquals(
                2
            )
    }

    @Test
    fun openedDialogDisplaysClickedStockValuation() {
        setPagerListContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                    peRatio =
                        "20",
                    dividendYield =
                        "2.5",
                    pbRatio =
                        "5",
                ),
        )

        openDialog(
            code =
                "2330",
        )

        /*
         * The interaction test only verifies that opening a row exposes
         * the valuation dialog and its valuation fields.
         *
         * Exact value rendering already belongs to
         * StockValuationDialogTest.
         */
        composeTestRule
            .onNodeWithText(
                "本益比"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "殖利率(%)"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "股價淨值比"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "2.5"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "5"
            )
            .assertIsDisplayed()
    }

    @Test
    fun closingValuationDialogRemovesDialog() {
        setPagerListContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                ),
        )

        openDialog(
            code =
                "2330",
        )

        composeTestRule
            .onNodeWithContentDescription(
                "關閉股票估值"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertDoesNotExist()

        /*
         * Closing the dialog does not remove the underlying stock row.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "開啟 2330 股票估值"
            )
            .assertIsDisplayed()
    }

    @Test
    fun clickingDifferentStockOpensCorrectDialog() {
        val first =
            stock(
                code =
                    "2330",
                name =
                    "台積電",
                peRatio =
                    "20",
            )

        val second =
            stock(
                code =
                    "2454",
                name =
                    "聯發科",
                peRatio =
                    "30",
            )

        setPagerListContent(
            stocks =
                listOf(
                    first,
                    second,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "開啟 2454 股票估值"
            )
            .performClick()

        /*
         * The dialog semantic is the authoritative assertion for which
         * stock was selected.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2454"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertDoesNotExist()

        /*
         * "聯發科" exists in both the row behind the dialog and the
         * dialog title.
         */
        composeTestRule
            .onAllNodesWithText(
                "聯發科"
            )
            .assertCountEquals(
                2
            )
    }

    /**
     * The valuation dialog is available from every Pager field because
     * clicking belongs to the stock row rather than one particular
     * Pager field.
     */
    @Test
    fun valuationDialogCanOpenFromNonValuationPage() {
        val closingPricePage =
            requireNotNull(
                stockPagerPages
                    .firstOrNull { page ->
                        page.key ==
                            StockPagerFieldKey
                                .CLOSING_PRICE
                    }
            )

        setPagerListContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                    peRatio =
                        "20",
                ),
            page =
                closingPricePage,
        )

        openDialog(
            code =
                "2330",
        )

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "本益比"
            )
            .assertIsDisplayed()
    }

    private fun openDialog(
        code: String,
    ) {
        composeTestRule
            .onNodeWithContentDescription(
                "開啟 $code 股票估值"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 $code"
            )
            .assertIsDisplayed()
    }

    private fun setPagerListContent(
        stock: Stock,
        page: StockPagerPage =
            defaultPage(),
    ) {
        setPagerListContent(
            stocks =
                listOf(
                    stock
                ),
            page =
                page,
        )
    }

    private fun setPagerListContent(
        stocks: List<Stock>,
        page: StockPagerPage =
            defaultPage(),
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                StockPagerList(
                    stocks =
                        stocks,
                    page =
                        page,
                    listState =
                        rememberLazyListState(),
                )
            }
        }
    }

    private fun stock(
        code: String,
        name: String,
        peRatio: String? = null,
        dividendYield: String? = null,
        pbRatio: String? = null,
    ): Stock {

        val hasValuation =
            peRatio != null ||
                dividendYield != null ||
                pbRatio != null

        return Stock(
            Code =
                code,
            Name =
                name,
            valuation =
                if (hasValuation) {
                    BwibbuItem(
                        Code =
                            code,
                        Name =
                            name,
                        PEratio =
                            peRatio
                                ?.let(
                                    ::BigDecimal
                                ),
                        DividendYield =
                            dividendYield
                                ?.let(
                                    ::BigDecimal
                                ),
                        PBratio =
                            pbRatio
                                ?.let(
                                    ::BigDecimal
                                ),
                    )
                } else {
                    null
                },
        )
    }

    private companion object {

        fun defaultPage():
            StockPagerPage {

            return requireNotNull(
                stockPagerPages
                    .firstOrNull { page ->
                        page.key ==
                            StockPagerFieldKey.PE_RATIO
                    }
            )
        }
    }
}
