package com.example.twsestockapp.data.remote.model

import java.math.BigDecimal

/**
 * Returns stock items whose closing price falls within
 * the specified inclusive range.
 *
 * The item type must implement both [StockBaseItem] and
 * [ClosingPriceItem].
 *
 * This project uses the `ext` prefix to distinguish extension
 * functions from actual member functions.
 *
 * Items without a valid closing price are excluded.
 *
 * Both boundaries are inclusive:
 *
 * `minPrice <= ClosingPrice <= maxPrice`
 *
 * @param minPrice Minimum closing price to include.
 * @param maxPrice Maximum closing price to include.
 *
 * @return A new list containing matching items.
 *
 * @throws IllegalArgumentException if [minPrice] is greater
 * than [maxPrice].
 */
fun <T> List<T>.extFindByClosingPriceRange(
    minPrice: BigDecimal,
    maxPrice: BigDecimal
): List<T>
    where T : StockBaseItem,
          T : ClosingPriceItem {

    require(minPrice <= maxPrice) {
        "minPrice must not be greater than maxPrice. " +
            "minPrice=$minPrice, maxPrice=$maxPrice"
    }

    return filter { item ->

        val price = item.ClosingPrice

        price != null &&
            price >= minPrice &&
            price <= maxPrice
    }
}