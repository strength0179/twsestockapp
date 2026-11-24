package com.example.twsestockapp.data.remote.model

/**
 * Extension function for lists containing [StockBaseItem] implementations.
 *
 * Finds the first item whose [StockBaseItem.Code] matches [code].
 *
 * This project uses the `ext` prefix for extension functions so that
 * extension functions can be distinguished from actual member functions
 * when reading source code without IDE navigation support.
 *
 * The concrete item type is preserved through generic type [T].
 * For example, calling this function on `List<StockDayItem>` returns
 * `StockDayItem?`, while calling it on `List<BwibbuItem>` returns
 * `BwibbuItem?`.
 *
 * @param code Stock code to search for.
 * @return The first matching item, or `null` if no matching item exists.
 */
fun <T : StockBaseItem> List<T>.extFindByCode(code: String): T? {
    return firstOrNull { it.Code == code }
}