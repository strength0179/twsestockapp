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
 *
 * Required valuation fields remain effectively visible even when their
 * persistent IsVisible value is false.
 */
class MainScreenFieldSelectionTest {

    @get:Rule
    val composeTestRule =
        createAndroidComposeRule<ComponentActivity>()

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
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 2"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 3"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 漲跌"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "x"
            )
            .assertIsDisplayed()
    }

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

    @Test
    fun hiddenOptionalFieldMovementButtonsAreDisabled() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "將 漲跌 往前移"
            )
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 漲跌 往後移"
            )
            .assertIsNotEnabled()
    }

    @Test
    fun visibleMovementButtonsUseEffectiveVisibleOrder() {
        setFieldEditorContent(
            state =
                visibilityState(),
        )

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

        composeTestRule
            .onNodeWithContentDescription(
                "將 本益比 往前移"
            )
            .assertIsEnabled()

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

    @Test
    fun finalOptionalVisibleFieldHideButtonIsDisabled() {
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
            .assertIsNotEnabled()
    }

    @Test
    fun requiredValuationHideButtonsAreDisabled() {
        setFieldEditorContent(
            state =
                requiredValuationState(
                    persistedVisible =
                        true,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "本益比 是必要欄位"
            )
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "殖利率 是必要欄位"
            )
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "股價淨值比 是必要欄位"
            )
            .assertIsNotEnabled()
    }

    @Test
    fun requiredValuationFieldsRemainReorderable() {
        setFieldEditorContent(
            state =
                requiredValuationState(
                    persistedVisible =
                        true,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "將 本益比 往前移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 本益比 往後移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 殖利率 往前移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 殖利率 往後移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 股價淨值比 往前移"
            )
            .assertIsEnabled()

        composeTestRule
            .onNodeWithContentDescription(
                "將 股價淨值比 往後移"
            )
            .assertIsNotEnabled()
    }

    @Test
    fun optionalFieldRemainsHideableBesideRequiredFields() {
        setFieldEditorContent(
            state =
                requiredValuationState(
                    persistedVisible =
                        true,
                ),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "隱藏 收盤價"
            )
            .assertIsEnabled()
    }

    /**
     * Required valuation fields must be rendered as visible even when
     * their persisted IsVisible values are false.
     */
    @Test
    fun persistedHiddenRequiredFieldsStillDisplayAsVisibleRows() {
        setFieldEditorContent(
            state =
                allPersistedHiddenState(),
        )

        composeTestRule
            .onNodeWithText(
                "本益比"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "殖利率"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "股價淨值比"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 1"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 2"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                "欄位顯示排序 3"
            )
            .assertIsDisplayed()
    }

    @Test
    fun persistedHiddenRequiredFieldsDoNotExposeRestoreControl() {
        setFieldEditorContent(
            state =
                allPersistedHiddenState(),
        )

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 本益比"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 殖利率"
            )
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithContentDescription(
                "顯示 股價淨值比"
            )
            .assertDoesNotExist()

        /*
         * Optional persisted-hidden field remains normally restorable.
         */
        composeTestRule
            .onNodeWithContentDescription(
                "顯示 收盤價"
            )
            .assertIsDisplayed()
    }

    @Test
    fun allPersistedHiddenFieldsStillAllowEditorCompletionWhenRequiredFieldsExist() {
        setFieldEditorContent(
            state =
                allPersistedHiddenState(),
        )

        composeTestRule
            .onNodeWithText(
                "完成"
            )
            .assertIsEnabled()
    }

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
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "完成"
            )
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

    private fun requiredValuationState(
        persistedVisible: Boolean,
    ): MainScreenUiState.Success {

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
                            "peRatio",
                        absoluteOrder =
                            2,
                        isVisible =
                            persistedVisible,
                    ),
                    field(
                        fieldKey =
                            "dividendYield",
                        absoluteOrder =
                            3,
                        isVisible =
                            persistedVisible,
                    ),
                    field(
                        fieldKey =
                            "pbRatio",
                        absoluteOrder =
                            4,
                        isVisible =
                            persistedVisible,
                    ),
                ),
        )
    }

    private fun allPersistedHiddenState():
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
                            false,
                    ),
                    field(
                        fieldKey =
                            "peRatio",
                        absoluteOrder =
                            2,
                        isVisible =
                            false,
                    ),
                    field(
                        fieldKey =
                            "dividendYield",
                        absoluteOrder =
                            3,
                        isVisible =
                            false,
                    ),
                    field(
                        fieldKey =
                            "pbRatio",
                        absoluteOrder =
                            4,
                        isVisible =
                            false,
                    ),
                ),
        )
    }

    private fun field(
        fieldKey: String,
        absoluteOrder: Int,
        isVisible: Boolean = true,
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
