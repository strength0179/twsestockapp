package com.example.twsestockapp.ui.main.sort

/**
 * Available stock-list sorting options.
 *
 * UI labels are intentionally kept outside the enum so application
 * logic never depends on display strings.
 */
enum class StockSortOption {

    /**
     * Preserve the stock order supplied by the current application
     * state.
     */
    Default,

    /**
     * Sort by price-to-earnings ratio.
     */
    PeRatio,

    /**
     * Sort by dividend yield percentage.
     */
    DividendYield,

    /**
     * Sort by price-to-book ratio.
     */
    PbRatio,
}
