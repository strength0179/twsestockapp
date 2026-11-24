package com.example.twsestockapp.ui.main.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.twsestockapp.ui.main.MainScreenContent
import com.example.twsestockapp.ui.main.MainScreenEditorMode
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme

/**
 * Preview of the mandatory stock-selection state when no stocks are
 * currently selected.
 */
@Preview(
    name = "Empty Stock Selection",
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun EmptyStockSelectionPreview() {
    TWSEStockAppTheme {
        MainScreenContent(
            state =
                PREVIEW_EMPTY_STOCK_SELECTION_STATE,
            editorMode =
                MainScreenEditorMode.StockSelection,
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
