package com.example.twsestockapp.ui.main.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.twsestockapp.ui.main.MainScreenContent
import com.example.twsestockapp.ui.main.MainScreenEditorMode
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme

/**
 * Preview of the ordered stock-selection editor.
 */
@Preview(
    name = "Stock Selection Editor",
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun StockSelectionPreview() {
    TWSEStockAppTheme {
        MainScreenContent(
            state =
                PREVIEW_MAIN_SCREEN_STATE,
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
