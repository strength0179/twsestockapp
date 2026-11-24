package com.example.twsestockapp.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.ui.main.selection.SELECTED_STOCKS_LIST_TAG
import com.example.twsestockapp.ui.main.selection.STOCK_CATALOG_LIST_TAG
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests the two-page stock-selection editor.
 *
 * Page 0:
 *
 * - preserves complete-stock order
 * - handles selection
 * - keeps its navigation title fixed above the scrolling list
 *
 * Page 1:
 *
 * - presents selections in DisplayOrder
 * - handles selected-stock ordering
 * - supports selected-stock removal
 * - prevents removal of the final selected stock
 * - keeps its navigation title fixed above the scrolling list
 */
class MainScreenStockSelectionTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun editorInitiallyDisplaysStockCatalogPage() {
        setStockEditorContent()

        composeTestRule
            .onNodeWithText(
                "已選股票 2"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "股票清單項目 2317"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "已選股票排序 1，2454"
            )
            .assertDoesNotExist()
    }

    @Test
    fun stockCatalogPreservesOriginalStockOrder() {
        setStockEditorContent()

        val stock2317 =
            catalogItemBounds(
                code =
                    "2317",
            )

        val stock2330 =
            catalogItemBounds(
                code =
                    "2330",
            )

        val stock2454 =
            catalogItemBounds(
                code =
                    "2454",
            )

        val stock2603 =
            catalogItemBounds(
                code =
                    "2603",
            )

        assertTrue(
            stock2317.top <
                stock2330.top
        )

        assertTrue(
            stock2330.top <
                stock2454.top
        )

        assertTrue(
            stock2454.top <
                stock2603.top
        )
    }

    @Test
    fun selectedStocksRemainAtOriginalCatalogPositions() {
        setStockEditorContent()

        composeTestRule
            .onNodeWithContentDescription(
                "2330 排序 2，點擊取消選取"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "2454 排序 1，點擊取消選取"
            )
            .assertIsDisplayed()

        val stock2330 =
            catalogItemBounds(
                code =
                    "2330",
            )

        val stock2454 =
            catalogItemBounds(
                code =
                    "2454",
            )

        assertTrue(
            stock2330.top <
                stock2454.top
        )
    }

    @Test
    fun clickingUnselectedStockRequestsSelection() {
        var selectedCode:
            String? = null

        setStockEditorContent(
            onSelect = { code ->
                selectedCode =
                    code
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "2317 尚未選取"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "2317",
                selectedCode,
            )
        }
    }

    @Test
    fun clickingSelectedStockRequestsUnselection() {
        var unselectedCode:
            String? = null

        setStockEditorContent(
            onUnselect = { code ->
                unselectedCode =
                    code
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "2454 排序 1，點擊取消選取"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "2454",
                unselectedCode,
            )
        }
    }

    @Test
    fun rightNavigationOpensSelectedStocksPageInDisplayOrder() {
        setStockEditorContent()

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithText(
                "繼續新增"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "已選股票 2"
            )
            .assertIsDisplayed()

        val first =
            selectedItemBounds(
                description =
                    "已選股票排序 1，2454",
            )

        val second =
            selectedItemBounds(
                description =
                    "已選股票排序 2，2330",
            )

        assertTrue(
            first.top <
                second.top
        )
    }

    @Test
    fun continueAddingReturnsToStockCatalogPage() {
        setStockEditorContent()

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "繼續新增股票"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "股票清單項目 2317"
            )
            .assertIsDisplayed()
    }

    @Test
    fun selectedPageForwardButtonRequestsMoveForward() {
        var movedCode:
            String? = null

        setStockEditorContent(
            onMoveForward = { code ->
                movedCode =
                    code
            },
        )

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2330 往前移"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "2330",
                movedCode,
            )
        }
    }

    @Test
    fun selectedPageBackwardButtonRequestsMoveBackward() {
        var movedCode:
            String? = null

        setStockEditorContent(
            onMoveBackward = { code ->
                movedCode =
                    code
            },
        )

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2454 往後移"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "2454",
                movedCode,
            )
        }
    }

    @Test
    fun selectedPageMovementButtonsRespectBoundaries() {
        setStockEditorContent()

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2454 往前移"
            )
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2454 往後移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2330 往前移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 2330 往後移"
            )
            .assertIsNotEnabled()
    }

    /**
     * Scrolling the catalog must never move the page-navigation title.
     *
     * The test also verifies that the title remains clickable after
     * list interaction.
     */
    @Test
    fun catalogTitleRemainsVisibleAndInteractiveAfterCatalogScroll() {
        setStockEditorContent()

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .assertIsDisplayed()

        repeat(3) {
            composeTestRule
                .onNodeWithTag(
                    STOCK_CATALOG_LIST_TAG
                )
                .performTouchInput {
                    swipeUp()
                }
        }

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "已選股票 2"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "繼續新增股票"
            )
            .assertIsDisplayed()
    }

    /**
     * The selected-stock title is structurally independent of its
     * LazyColumn and remains available as a navigation action.
     */
    @Test
    fun selectedTitleRemainsAvailableAboveSelectedList() {
        setStockEditorContent()

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "繼續新增股票"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "已選股票 2"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(
                SELECTED_STOCKS_LIST_TAG
            )
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription(
                "繼續新增股票"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .assertIsDisplayed()
    }

    /**
     * When multiple stocks are selected, each selected-stock row exposes
     * an enabled remove control.
     */
    @Test
    fun selectedPageRemoveButtonsAreEnabledWhenMultipleStocksRemain() {
        setStockEditorContent()

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "移除自選股票 2454"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "移除自選股票 2330"
            )
            .assertIsEnabled()
    }

    /**
     * The selected-stock remove control reuses the existing unselect
     * callback.
     */
    @Test
    fun selectedPageRemoveButtonRequestsUnselection() {
        var removedCode:
            String? = null

        setStockEditorContent(
            onUnselect = { code ->
                removedCode =
                    code
            },
        )

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "移除自選股票 2454"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "2454",
                removedCode,
            )
        }
    }

    /**
     * Stock selection must never become empty through the selected-stock
     * page.
     *
     * When only one stock remains, its remove control stays visible but
     * is disabled.
     */
    @Test
    fun finalSelectedStockCannotBeRemoved() {
        val selectedStock =
            MAIN_SCREEN_TEST_STOCKS
                .first { stock ->
                    stock.Code ==
                        "2454"
                }

        val singleSelectionState =
            MAIN_SCREEN_TEST_STATE.copy(
                visibleStocks =
                    listOf(
                        selectedStock
                    ),
                selections =
                    listOf(
                        StockSelection(
                            Code =
                                "2454",
                            DisplayOrder =
                                1,
                        )
                    ),
            )

        setStockEditorContent(
            state =
                singleSelectionState,
        )

        openSelectedStocksPage()

        composeTestRule
            .onNodeWithContentDescription(
                "移除自選股票 2454"
            )
            .assertIsNotEnabled()
    }

    private fun openSelectedStocksPage() {
        composeTestRule
            .onNodeWithContentDescription(
                "查看已選股票"
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(
                "繼續新增股票"
            )
            .assertIsDisplayed()
    }

    private fun catalogItemBounds(
        code: String,
    ): Rect {

        return composeTestRule
            .onNodeWithContentDescription(
                "股票清單項目 $code"
            )
            .fetchSemanticsNode()
            .boundsInRoot
    }

    private fun selectedItemBounds(
        description: String,
    ): Rect {

        return composeTestRule
            .onNodeWithContentDescription(
                description
            )
            .fetchSemanticsNode()
            .boundsInRoot
    }

    private fun setStockEditorContent(
        state:
            MainScreenUiState.Success =
            MAIN_SCREEN_TEST_STATE,
        onSelect:
            (String) -> Unit = {},
        onUnselect:
            (String) -> Unit = {},
        onMoveForward:
            (String) -> Unit = {},
        onMoveBackward:
            (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                MainScreenContent(
                    state =
                        state,
                    editorMode =
                        MainScreenEditorMode
                            .StockSelection,
                    onOpenStockEditor = {},
                    onOpenFieldEditor = {},
                    onCloseEditor = {},
                    onSelectStock =
                        onSelect,
                    onUnselectStock =
                        onUnselect,
                    onMoveStockForward =
                        onMoveForward,
                    onMoveStockBackward =
                        onMoveBackward,
                    onShowField = {},
                    onHideField = {},
                    onMoveFieldForward = {},
                    onMoveFieldBackward = {},
                )
            }
        }
    }
}
