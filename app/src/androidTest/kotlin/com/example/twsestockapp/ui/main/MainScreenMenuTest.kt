package com.example.twsestockapp.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests the normal-mode editing entry points.
 *
 * - stock editing uses the bottom-right edit button
 * - field editing uses the top-right overflow menu
 */
class MainScreenMenuTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bottomRightEditButtonRequestsOpeningStockEditor() {
        var requested =
            false

        setNormalContent(
            onOpenStockEditor = {
                requested =
                    true
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "編輯自選股票"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(
                requested
            )
        }
    }

    @Test
    fun menuRequestsOpeningFieldEditor() {
        var requested =
            false

        setNormalContent(
            onOpenFieldEditor = {
                requested =
                    true
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "開啟主畫面選單"
            )
            .performClick()

        composeTestRule
            .onNodeWithText(
                "編輯資料欄位"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertTrue(
                requested
            )
        }
    }

    @Test
    fun menuDoesNotContainStockEditorEntry() {
        setNormalContent()

        composeTestRule
            .onNodeWithContentDescription(
                "開啟主畫面選單"
            )
            .performClick()

        composeTestRule
            .onNodeWithText(
                "編輯自選股票"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(
                "編輯資料欄位"
            )
            .assertExists()
    }

    private fun setNormalContent(
        onOpenStockEditor:
            () -> Unit = {},
        onOpenFieldEditor:
            () -> Unit = {},
    ) {
        composeTestRule.setContent {
            TWSEStockAppTheme {
                MainScreenContent(
                    state =
                        MAIN_SCREEN_TEST_STATE,
                    editorMode =
                        MainScreenEditorMode.None,
                    onOpenStockEditor =
                        onOpenStockEditor,
                    onOpenFieldEditor =
                        onOpenFieldEditor,
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

