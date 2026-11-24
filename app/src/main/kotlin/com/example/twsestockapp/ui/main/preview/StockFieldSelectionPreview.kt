package com.example.twsestockapp.ui.main.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.twsestockapp.ui.main.MainScreenContent
import com.example.twsestockapp.ui.main.MainScreenEditorMode
import com.example.twsestockapp.ui.theme.TWSEStockAppTheme

/**
 * Preview of the Pager-field visibility and ordering editor.
 */
@Preview(
    name = "Field Selection Editor",
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun StockFieldSelectionPreview() {
    TWSEStockAppTheme {
        MainScreenContent(
            state =
                PREVIEW_MAIN_SCREEN_STATE,
            editorMode =
                MainScreenEditorMode.FieldSelection,
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
