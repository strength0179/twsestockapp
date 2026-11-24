package com.example.twsestockapp.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Rule
import org.junit.Test

/**
 * Tests normal MainScreen presentation.
 *
 * These tests verify:
 *
 * - selected stocks are displayed in persistent order
 * - Pager fields are resolved from fieldSelections
 * - DisplayOrder determines Pager order
 * - only selected fields become Pager pages
 * - horizontal gestures change fields
 * - Pager navigation wraps around at both ends
 * - Pager indicator follows the current page
 */
class MainScreenNormalModeTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun primaryPagerDisplaysOrderedVisibleStocks() {
        setNormalContent(
            state =
                MAIN_SCREEN_TEST_STATE,
        )

        composeTestRule
            .onNodeWithText(
                "收盤價"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                "2454"
            )[0]
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                "聯發科"
            )[0]
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                "2330"
            )[0]
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText(
                "台積電"
            )[0]
            .assertExists()
            .assertIsDisplayed()

        /*
         * 2317 exists in allStocks but is not selected.
         */
        composeTestRule
            .onNodeWithText(
                "2317"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "鴻海"
            )
            .assertDoesNotExist()
    }

    /**
     * Persistent DisplayOrder determines the initial Pager page.
     */
    @Test
    fun pagerUsesSelectedFieldDisplayOrder() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    twoFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithText(
                "成交量"
            )
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Fields not present in fieldSelections must not become Pager pages.
     */
    @Test
    fun pagerOnlyContainsSelectedFields() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    listOf(
                        StockFieldSelection(
                            FieldKey =
                                "tradeVolume",
                            DisplayOrder =
                                1,
                        ),
                    ),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithText(
                "成交量"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "收盤價"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "漲跌"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "漲跌幅"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "月均價"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "本益比"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "殖利率"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "股價淨值比"
            )
            .assertDoesNotExist()
    }

    /**
     * Empty field configuration must not crash the Pager.
     */
    @Test
    fun emptyFieldSelectionDisplaysFallbackMessage() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    emptyList(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithText(
                "請選擇至少一個資料欄位"
            )
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Swipe left requests the next configured Pager field.
     *
     * Effective order:
     *
     * 1. tradeVolume
     * 2. closingPrice
     */
    @Test
    fun swipeLeftChangesToNextConfiguredField() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    twoFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithText(
                "成交量"
            )
            .assertIsDisplayed()

        swipeLeft()

        composeTestRule
            .onNodeWithText(
                "收盤價"
            )
            .assertIsDisplayed()
    }

    /**
     * Swipe right from the first page wraps to the final page.
     */
    @Test
    fun swipeRightFromFirstFieldWrapsToLastField() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    twoFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithText(
                "成交量"
            )
            .assertIsDisplayed()

        swipeRight()

        composeTestRule
            .onNodeWithText(
                "收盤價"
            )
            .assertIsDisplayed()
    }

    /**
     * Swipe left from the final page wraps to the first page.
     */
    @Test
    fun swipeLeftFromLastFieldWrapsToFirstField() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    twoFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        swipeLeft()

        composeTestRule
            .onNodeWithText(
                "收盤價"
            )
            .assertIsDisplayed()

        swipeLeft()

        composeTestRule
            .onNodeWithText(
                "成交量"
            )
            .assertIsDisplayed()
    }

    /**
     * A three-page Pager initially reports page 1 of 3.
     */
    @Test
    fun indicatorInitiallyShowsFirstPage() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    threeFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 1 / 3"
            )
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Swiping from the first page to the second page updates the
     * indicator from 1 / 3 to 2 / 3.
     */
    @Test
    fun swipeLeftUpdatesIndicatorToSecondPage() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    threeFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 1 / 3"
            )
            .assertIsDisplayed()

        swipeLeft()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 2 / 3"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 1 / 3"
            )
            .assertDoesNotExist()
    }

    /**
     * Swiping left from the final page wraps to the first page and
     * updates the indicator back to 1 / 3.
     */
    @Test
    fun swipeLeftFromFinalPageWrapsIndicatorToFirstPage() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    threeFieldSelections(),
            )

        setNormalContent(
            state =
                state,
        )

        /*
         * Page 1 -> Page 2.
         */
        swipeLeft()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 2 / 3"
            )
            .assertIsDisplayed()

        /*
         * Page 2 -> Page 3.
         */
        swipeLeft()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 3 / 3"
            )
            .assertIsDisplayed()

        /*
         * Page 3 -> Page 1 through circular navigation.
         */
        swipeLeft()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 1 / 3"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "Pager 頁面 3 / 3"
            )
            .assertDoesNotExist()
    }

    /**
     * Two-field configuration used by basic swipe tests.
     *
     * Effective Pager order:
     *
     * 1. tradeVolume
     * 2. closingPrice
     */
    private fun twoFieldSelections():
        List<StockFieldSelection> {

        return listOf(
            StockFieldSelection(
                FieldKey =
                    "closingPrice",
                DisplayOrder =
                    2,
            ),
            StockFieldSelection(
                FieldKey =
                    "tradeVolume",
                DisplayOrder =
                    1,
            ),
        )
    }

    /**
     * Three-field configuration used by indicator tests.
     *
     * The input List order intentionally differs from DisplayOrder.
     *
     * Effective Pager order:
     *
     * 1. tradeVolume
     * 2. closingPrice
     * 3. peRatio
     */
    private fun threeFieldSelections():
        List<StockFieldSelection> {

        return listOf(
            StockFieldSelection(
                FieldKey =
                    "peRatio",
                DisplayOrder =
                    3,
            ),
            StockFieldSelection(
                FieldKey =
                    "closingPrice",
                DisplayOrder =
                    2,
            ),
            StockFieldSelection(
                FieldKey =
                    "tradeVolume",
                DisplayOrder =
                    1,
            ),
        )
    }

    /**
     * Performs one left swipe across the MainScreen root.
     */
    private fun swipeLeft() {
        composeTestRule
            .onRoot()
            .performTouchInput {
                swipeLeft()
            }
    }

    /**
     * Performs one right swipe across the MainScreen root.
     */
    private fun swipeRight() {
        composeTestRule
            .onRoot()
            .performTouchInput {
                swipeRight()
            }
    }

    /**
     * Sets MainScreenContent in normal non-editor mode.
     */
    private fun setNormalContent(
        state: MainScreenUiState.Success,
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                MainScreenContent(
                    state =
                        state,
                    editorMode =
                        MainScreenEditorMode.None,
                    onOpenStockEditor = {},
                    onOpenFieldEditor = {},
                    onCloseEditor = {},
                    onSelectStock = {},
                    onUnselectStock = {},
                    onMoveStockForward = {},
                    onMoveStockBackward = {},
                    onShowField = {},
                    onHideField = {},
                    onMoveFieldForward = {},
                    onMoveFieldBackward = {},
                )
            }
        }
    }
}

