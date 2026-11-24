package com.example.twsestockapp.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests Pager-field visibility and ordering presentation.
 */
class MainScreenFieldSelectionTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

    /**
     * Absolute persistence:
     *
     * closingPrice 1 visible
     * change       2 hidden
     * tradeVolume  3 visible
     * peRatio      4 visible
     *
     * Editor:
     *
     * closingPrice 1
     * tradeVolume  2
     * peRatio      3
     * change       x
     */
    @Test
    fun editorDisplaysVisibleFieldsFirstWithCompactVisibleOrder() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 1"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 2"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 3"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 漲跌"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "x"
            )
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Clicking a hidden field's x box requests show without changing
     * its persistent absolute order in the UI layer.
     */
    @Test
    fun clickingHiddenXRequestsShow() {
        var shownField:
            String? = null

        setFieldEditorContent(
            state =
                visibilityState(),
            onShowField = { fieldKey ->
                shownField =
                    fieldKey
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 漲跌"
            )
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "change",
                shownField,
            )
        }
    }

    /**
     * Clicking the circular multiplication button requests hide.
     */
    @Test
    fun clickingCircularXRequestsHide() {
        var hiddenField:
            String? = null

        setFieldEditorContent(
            state =
                visibilityState(),
            onHideField = { fieldKey ->
                hiddenField =
                    fieldKey
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "隱藏 成交量"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "tradeVolume",
                hiddenField,
            )
        }
    }

    /**
     * Hidden fields remain in the editor but cannot be reordered
     * directly.
     */
    @Test
    fun hiddenFieldMovementButtonsAreDisabled() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "將 漲跌 往前移"
            )
            .assertExists()
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 漲跌 往後移"
            )
            .assertExists()
            .assertIsNotEnabled()
    }

    /**
     * Movement controls are determined from visible order, not absolute
     * DisplayOrder.
     */
    @Test
    fun visibleMovementButtonsUseVisibleOrder() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

        /*
         * closingPrice is the first visible field.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "將 收盤價 往前移"
            )
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 收盤價 往後移"
            )
            .assertIsEnabled()

        /*
         * tradeVolume is the middle visible field even though its
         * absolute DisplayOrder is 3.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "將 成交量 往前移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 成交量 往後移"
            )
            .assertIsEnabled()

        /*
         * peRatio is the final visible field.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "將 本益比 往後移"
            )
            .assertIsNotEnabled()
    }

    @Test
    fun plusButtonRequestsMoveForward() {
        var movedField:
            String? = null

        setFieldEditorContent(
            state =
                visibilityState(),
            onMoveForward = { fieldKey ->
                movedField =
                    fieldKey
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "將 成交量 往前移"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "tradeVolume",
                movedField,
            )
        }
    }

    @Test
    fun minusButtonRequestsMoveBackward() {
        var movedField:
            String? = null

        setFieldEditorContent(
            state =
                visibilityState(),
            onMoveBackward = { fieldKey ->
                movedField =
                    fieldKey
            },
        )

        composeTestRule
            .onNodeWithContentDescription(
                "將 收盤價 往後移"
            )
            .assertIsEnabled()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "closingPrice",
                movedField,
            )
        }
    }

    /**
     * The final remaining visible field cannot be hidden.
     */
    @Test
    fun finalVisibleFieldHideButtonIsDisabled() {
        val state =
            MAIN_SCREEN_TEST_STATE.copy(
                fieldSelections =
                    listOf(
                        field(
                            fieldKey =
                                "closingPrice",
                            absoluteOrder =
                                1,
                            isVisible =
                                true,
                        ),
                        field(
                            fieldKey =
                                "change",
                            absoluteOrder =
                                2,
                            isVisible =
                                false,
                        ),
                    ),
            )

        setFieldEditorContent(
            state =
                state,
        )

        composeTestRule
            .onNodeWithContentDescription(
                "收盤價 是最後一個顯示欄位"
            )
            .assertExists()
            .assertIsNotEnabled()
    }

    /**
     * Field editor title and completion action remain available.
     */
    @Test
    fun fieldEditorDisplaysTitleAndCompletionAction() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

        composeTestRule
            .onNodeWithText(
                "編輯資料欄位"
            )
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "完成"
            )
            .assertExists()
            .assertIsEnabled()
    }

    private fun visibilityState():
        MainScreenUiState.Success {

        return MAIN_SCREEN_TEST_STATE.copy(
            fieldSelections =
                listOf(
                    field(
                        fieldKey =
                            "closingPrice",
                        absoluteOrder =
                            1,
                        isVisible =
                            true,
                    ),
                    field(
                        fieldKey =
                            "change",
                        absoluteOrder =
                            2,
                        isVisible =
                            false,
                    ),
                    field(
                        fieldKey =
                            "tradeVolume",
                        absoluteOrder =
                            3,
                        isVisible =
                            true,
                    ),
                    field(
                        fieldKey =
                            "peRatio",
                        absoluteOrder =
                            4,
                        isVisible =
                            true,
                    ),
                ),
        )
    }

    private fun field(
        fieldKey: String,
        absoluteOrder: Int,
        isVisible: Boolean,
    ): StockFieldSelection {

        return StockFieldSelection(
            FieldKey =
                fieldKey,
            DisplayOrder =
                absoluteOrder,
            IsVisible =
                isVisible,
        )
    }

    private fun setFieldEditorContent(
        state: MainScreenUiState.Success,
        onShowField:
            (String) -> Unit = {},
        onHideField:
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
                            .FieldSelection,
                    onOpenStockEditor = {},
                    onOpenFieldEditor = {},
                    onCloseEditor = {},
                    onSelectStock = {},
                    onUnselectStock = {},
                    onMoveStockForward = {},
                    onMoveStockBackward = {},

                    /*
                     * Current compatibility mapping:
                     *
                     * onShowField   -> show
                     * onHideField -> hide
                     */
                    onShowField =
                        onShowField,
                    onHideField =
                        onHideField,
                    onMoveFieldForward =
                        onMoveForward,
                    onMoveFieldBackward =
                        onMoveBackward,
                )
            }
        }
    }
}

