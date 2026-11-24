package com.example.twsestockapp.ui.main

/**
 * Editor currently displayed by the main screen.
 */
internal enum class MainScreenEditorMode {

    /**
     * Normal stock-screen presentation.
     */
    None,

    /**
     * User-stock selection and ordering editor.
     */
    StockSelection,

    /**
     * Pager-field selection and ordering editor.
     */
    FieldSelection,
}
