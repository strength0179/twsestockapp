package com.example.twsestockapp.ui.main.component

import androidx.activity.ComponentActivity
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

/**
 * Tests StockValuationDialog independently from StockPager interaction.
 */
class StockValuationDialogTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dialogDisplaysStockIdentity() {
        setDialogContent(
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

        composeTestRule
            .onNodeWithContentDescription(
                "股票估值 2330"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "台積電"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "2330"
            )
            .assertIsDisplayed()
    }

    @Test
    fun dialogDisplaysAllValuationLabels() {
        setDialogContent(
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
    }

    @Test
    fun dialogDisplaysFormattedValuationValues() {
        setDialogContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                    peRatio =
                        "20.000",
                    dividendYield =
                        "2.500",
                    pbRatio =
                        "5.2500",
                ),
        )

        /*
         * Trailing decimal zeroes are intentionally removed.
         */
        composeTestRule
            .onNodeWithText(
                "20"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "2.5"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "5.25"
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingValuationValuesDisplayNoDataText() {
        setDialogContent(
            stock =
                Stock(
                    Code =
                        "2330",
                    Name =
                        "台積電",
                    valuation =
                        null,
                ),
        )

        /*
         * All three valuation fields have no value.
         */
        composeTestRule
            .onAllNodesWithText(
                "--"
            )
            .assertCountEquals(
                3
            )
    }

    @Test
    fun partiallyMissingValuationDisplaysOnlyMissingFieldsAsNoData() {
        setDialogContent(
            stock =
                stock(
                    code =
                        "2330",
                    name =
                        "台積電",
                    peRatio =
                        "20",
                    dividendYield =
                        null,
                    pbRatio =
                        "5",
                ),
        )

        composeTestRule
            .onNodeWithText(
                "20"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "5"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "--"
            )
            .assertIsDisplayed()
    }

    @Test
    fun closeButtonRequestsDismiss() {
        var dismissed =
            false

        setDialogContent(
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
            onDismissRequest = {
                dismissed =
                    true
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "關閉股票估值"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(
                dismissed
            )
        }
    }

    private fun setDialogContent(
        stock: Stock,
        onDismissRequest:
            () -> Unit = {},
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                StockValuationDialog(
                    stock =
                        stock,
                    onDismissRequest =
                        onDismissRequest,
                )
            }
        }
    }

    private fun stock(
        code: String,
        name: String,
        peRatio: String?,
        dividendYield: String?,
        pbRatio: String?,
    ): Stock {

        return Stock(
            Code =
                code,
            Name =
                name,
            valuation =
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
                ),
        )
    }
}

