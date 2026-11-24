package com.example.twsestockapp.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey

/**
 * Stateful main-screen entry point.
 */
@Composable
internal fun MainScreenScreen(
    viewModel: MainScreenViewModel,
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by
        viewModel.uiState
            .collectAsStateWithLifecycle()

    var editorMode by
        rememberSaveable {
            mutableStateOf(
                MainScreenEditorMode.None
            )
        }

    when (val state = uiState) {

        MainScreenUiState.Loading -> {
            MainScreenLoading(
                modifier =
                    modifier,
            )
        }

        is MainScreenUiState.Success -> {
            val effectiveEditorMode =
                if (
                    state.showSelectionPanel
                ) {
                    MainScreenEditorMode
                        .StockSelection
                } else {
                    editorMode
                }

            MainScreenContent(
                state =
                    state,
                editorMode =
                    effectiveEditorMode,

                onOpenStockEditor = {
                    editorMode =
                        MainScreenEditorMode
                            .StockSelection
                },

                onOpenFieldEditor = {
                    editorMode =
                        MainScreenEditorMode
                            .FieldSelection
                },

                onCloseEditor = {
                    val canClose =
                        when (
                            effectiveEditorMode
                        ) {
                            MainScreenEditorMode
                                .StockSelection ->
                                state.selections
                                    .isNotEmpty()

                            MainScreenEditorMode
                                .FieldSelection ->
                                state.fieldSelections
                                    .any { selection ->
                                        selection.IsVisible
                                    }

                            MainScreenEditorMode
                                .None ->
                                true
                        }

                    if (canClose) {
                        editorMode =
                            MainScreenEditorMode.None
                    }
                },

                onSelectStock = { code ->
                    editorMode =
                        MainScreenEditorMode
                            .StockSelection

                    viewModel.selectStock(
                        code =
                            code,
                    )
                },

                onUnselectStock = { code ->
                    viewModel.unselectStock(
                        code =
                            code,
                    )
                },

                onMoveStockForward = { code ->
                    viewModel.moveStockForward(
                        code =
                            code,
                    )
                },

                onMoveStockBackward = { code ->
                    viewModel.moveStockBackward(
                        code =
                            code,
                    )
                },

                onShowField = { fieldKey ->
                    editorMode =
                        MainScreenEditorMode
                            .FieldSelection

                    viewModel.showStockField(
                        fieldKey =
                            fieldKey,
                    )
                },

                onHideField = { fieldKey ->
                    viewModel.hideStockField(
                        fieldKey =
                            fieldKey,
                    )
                },

                onMoveFieldForward = { fieldKey ->
                    viewModel.moveStockFieldForward(
                        fieldKey =
                            fieldKey,
                    )
                },

                onMoveFieldBackward = { fieldKey ->
                    viewModel.moveStockFieldBackward(
                        fieldKey =
                            fieldKey,
                    )
                },

                onSelectStockSortOption = { option ->
                    viewModel.selectStockSortOption(
                        option =
                            option,
                    )
                },

                modifier =
                    modifier,
            )
        }

        is MainScreenUiState.Error -> {
            MainScreenError(
                throwable =
                    state.throwable,
                modifier =
                    modifier,
            )
        }
    }
}
