package com.example.twsestockapp.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.twsestockapp.ui.main.sort.StockSortOption
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests the main-screen stock sorting DropdownMenu.
 */
class MainScreenFilterMenuTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun normalModeDisplaysFilterButton() {
        setContent()

        composeTestRule
            .onNodeWithContentDescription(
                "開啟股票排序選單"
            )
            .assertIsDisplayed()
    }

    @Test
    fun filterMenuDisplaysAllFourSortOptions() {
        setContent()

        openFilterMenu()

        composeTestRule
            .onNodeWithText(
                "預設"
            )
            .assertIsDisplayed()

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
    fun currentSortOptionIsMarkedSelected() {
        setContent(
            state =
                MAIN_SCREEN_TEST_STATE.copy(
                    stockSortOption =
                        StockSortOption.PeRatio,
                ),
        )

        openFilterMenu()

        composeTestRule
            .onNodeWithContentDescription(
                "股票排序 本益比，已選取"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "股票排序 預設"
            )
            .assertIsDisplayed()
    }

    @Test
    fun selectingSortOptionRequestsSelectedOption() {
        var requested:
            StockSortOption? = null

        setContent(
            onSelectStockSortOption = { option ->
                requested =
                    option
            },
        )

        openFilterMenu()

        composeTestRule
            .onNodeWithText(
                "殖利率(%)"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                StockSortOption.DividendYield,
                requested,
            )
        }
    }

    @Test
    fun selectingOptionClosesFilterMenu() {
        setContent()

        openFilterMenu()

        composeTestRule
            .onNodeWithText(
                "股價淨值比"
            )
            .performClick()

        composeTestRule
            .onNodeWithText(
                "本益比"
            )
            .assertDoesNotExist()
    }

    @Test
    fun stockEditorDoesNotDisplayFilterButton() {
        setContent(
            editorMode =
                MainScreenEditorMode
                    .StockSelection,
        )

        composeTestRule
            .onNodeWithContentDescription(
                "開啟股票排序選單"
            )
            .assertDoesNotExist()
    }

    @Test
    fun fieldEditorDoesNotDisplayFilterButton() {
        setContent(
            editorMode =
                MainScreenEditorMode
                    .FieldSelection,
        )

        composeTestRule
            .onNodeWithContentDescription(
                "開啟股票排序選單"
            )
            .assertDoesNotExist()
    }

    private fun openFilterMenu() {
        composeTestRule
            .onNodeWithContentDescription(
                "開啟股票排序選單"
            )
            .performClick()
    }

    private fun setContent(
        state:
            MainScreenUiState.Success =
            MAIN_SCREEN_TEST_STATE,
        editorMode:
            MainScreenEditorMode =
            MainScreenEditorMode.None,
        onSelectStockSortOption:
            (StockSortOption) -> Unit = {},
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                MainScreenContent(
                    state =
                        state,
                    editorMode =
                        editorMode,
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
                    onSelectStockSortOption =
                        onSelectStockSortOption,
                )
            }
        }
    }
}
