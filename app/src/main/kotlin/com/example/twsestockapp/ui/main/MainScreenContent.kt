package com.example.twsestockapp.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.twse.stock.R
import com.example.twsestockapp.ui.main.component.StockList
import com.example.twsestockapp.ui.main.component.hasEffectivelyVisibleStockPagerField
import com.example.twsestockapp.ui.main.selection.StockFieldSelectionPanel
import com.example.twsestockapp.ui.main.selection.StockSelectionPanel
import com.example.twsestockapp.ui.main.sort.StockSortOption

/**
 * Renders a successful main-screen state.
 *
 * Normal mode:
 *
 * - bottom-right edit button opens stock selection
 * - Filter menu controls main Pager stock sorting
 * - overflow menu opens Pager-field editing
 *
 * Required valuation Pager fields are treated as visible regardless of
 * an older persisted IsVisible=false value.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreenContent(
    state: MainScreenUiState.Success,
    editorMode: MainScreenEditorMode,
    onOpenStockEditor: () -> Unit,
    onOpenFieldEditor: () -> Unit,
    onCloseEditor: () -> Unit,
    onSelectStock: (String) -> Unit,
    onUnselectStock: (String) -> Unit,
    onMoveStockForward: (String) -> Unit,
    onMoveStockBackward: (String) -> Unit,
    onShowField: (String) -> Unit,
    onHideField: (String) -> Unit,
    onMoveFieldForward: (String) -> Unit,
    onMoveFieldBackward: (String) -> Unit,
    onSelectStockSortOption:
        (StockSortOption) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isEditing =
        editorMode !=
            MainScreenEditorMode.None

    Scaffold(
        modifier =
            modifier,
        topBar = {
            MainScreenTopBar(
                editorMode =
                    editorMode,
                canCloseEditor =
                    canCloseMainScreenEditor(
                        state =
                            state,
                        editorMode =
                            editorMode,
                    ),
                stockSortOption =
                    state.stockSortOption,
                onSelectStockSortOption =
                    onSelectStockSortOption,
                onOpenFieldEditor =
                    onOpenFieldEditor,
                onCloseEditor =
                    onCloseEditor,
            )
        },
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(
                    onClick =
                        onOpenStockEditor,
                    modifier =
                        Modifier.semantics {
                            contentDescription =
                                "編輯自選股票"
                        },
                ) {
                    Text(
                        text =
                            "編輯",
                    )
                }
            }
        },
    ) { innerPadding ->
        when (editorMode) {

            MainScreenEditorMode.None -> {
                StockList(
                    stocks =
                        state.visibleStocks,
                    fieldSelections =
                        state.fieldSelections,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            ),
                )
            }

            MainScreenEditorMode.StockSelection -> {
                StockSelectionPanel(
                    stocks =
                        state.allStocks,
                    selections =
                        state.selections,
                    onSelect =
                        onSelectStock,
                    onUnselect =
                        onUnselectStock,
                    onMoveForward =
                        onMoveStockForward,
                    onMoveBackward =
                        onMoveStockBackward,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            ),
                )
            }

            MainScreenEditorMode.FieldSelection -> {
                StockFieldSelectionPanel(
                    selections =
                        state.fieldSelections,
                    onShow =
                        onShowField,
                    onHide =
                        onHideField,
                    onMoveForward =
                        onMoveFieldForward,
                    onMoveBackward =
                        onMoveFieldBackward,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            ),
                )
            }
        }
    }
}

/**
 * Main-screen application bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenTopBar(
    editorMode: MainScreenEditorMode,
    canCloseEditor: Boolean,
    stockSortOption: StockSortOption,
    onSelectStockSortOption:
        (StockSortOption) -> Unit,
    onOpenFieldEditor: () -> Unit,
    onCloseEditor: () -> Unit,
) {
    val isEditing =
        editorMode !=
            MainScreenEditorMode.None

    CenterAlignedTopAppBar(
        title = {
            Text(
                text =
                    when (editorMode) {
                        MainScreenEditorMode.None ->
                            stringResource(
                                R.string.app_name
                            )

                        MainScreenEditorMode
                            .StockSelection ->
                            "編輯自選股票"

                        MainScreenEditorMode
                            .FieldSelection ->
                            "編輯資料欄位"
                    }
            )
        },
        actions = {
            if (isEditing) {
                TextButton(
                    onClick =
                        onCloseEditor,
                    enabled =
                        canCloseEditor,
                ) {
                    Text(
                        text =
                            "完成",
                    )
                }
            } else {
                MainScreenFilterMenu(
                    selectedOption =
                        stockSortOption,
                    onSelectOption =
                        onSelectStockSortOption,
                )

                MainScreenMenu(
                    onEditFields =
                        onOpenFieldEditor,
                )
            }
        },
        colors =
            TopAppBarDefaults
                .topAppBarColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primaryContainer,
                    titleContentColor =
                        MaterialTheme
                            .colorScheme
                            .primary,
                ),
    )
}

/**
 * Determines whether the active editor may be closed.
 */
private fun canCloseMainScreenEditor(
    state: MainScreenUiState.Success,
    editorMode: MainScreenEditorMode,
): Boolean {

    return when (editorMode) {
        MainScreenEditorMode.None ->
            true

        MainScreenEditorMode.StockSelection ->
            state.selections
                .isNotEmpty()

        MainScreenEditorMode.FieldSelection ->
            hasEffectivelyVisibleStockPagerField(
                selections =
                    state.fieldSelections,
            )
    }
}
