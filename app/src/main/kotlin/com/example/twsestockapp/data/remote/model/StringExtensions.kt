package com.example.twsestockapp.data.remote.model

import java.math.BigDecimal

/**
 * Converts a numeric String into [BigDecimal].
 *
 * Comma separators and leading/trailing whitespace are removed
 * before conversion.
 *
 * This project uses the `ext` prefix to explicitly identify
 * extension functions.
 *
 * Examples:
 *
 * `"123.45"` -> `BigDecimal("123.45")`
 *
 * `"1,234.56"` -> `BigDecimal("1234.56")`
 *
 * Invalid or empty values return `null`.
 *
 * @return Converted [BigDecimal], or `null` when conversion fails.
 */
fun String.extToBigDecimalOrNull(): BigDecimal? {
    return replace(",", "")
        .trim()
        .toBigDecimalOrNull()
}