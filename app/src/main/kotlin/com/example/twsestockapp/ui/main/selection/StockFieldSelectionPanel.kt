package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Editor for Pager-field visibility and ordering.
 *
 * Presentation state is delegated to
 * [createStockFieldSelectionItemStates].
 *
 * Visibility operations are explicitly named show/hide. Neither
 * operation removes a persistent field or directly changes its absolute
 * DisplayOrder.
 */
@Composable
internal fun StockFieldSelectionPanel(
    selections: List<StockFieldSelection>,
    onShow: (String) -> Unit,
    onHide: (String) -> Unit,
    onMoveForward: (String) -> Unit,
    onMoveBackward: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemStates =
        createStockFieldSelectionItemStates(
            selections =
                selections,
        )

    LazyColumn(
        modifier =
            modifier.fillMaxSize(),
    ) {
        items(
            items =
                itemStates,
            key = { state ->
                state.page.key
            },
        ) { state ->
            StockFieldSelectionItem(
                state =
                    state,
                onShow = {
                    onShow(
                        state.selection.FieldKey
                    )
                },
                onHide = {
                    onHide(
                        state.selection.FieldKey
                    )
                },
                onMoveForward = {
                    onMoveForward(
                        state.selection.FieldKey
                    )
                },
                onMoveBackward = {
                    onMoveBackward(
                        state.selection.FieldKey
                    )
                },
            )
        }
    }
}
