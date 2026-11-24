package com.example.twsestockapp.data.remote.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.math.BigDecimal

/**
 * Gson TypeAdapter for converting TWSE numeric JSON values
 * into [BigDecimal].
 *
 * TWSE API numeric fields may be represented as JSON strings.
 * This adapter normalizes those values before converting them
 * into [BigDecimal].
 *
 * Supported input examples:
 *
 * `"123.45"`   -> BigDecimal("123.45")
 * `"1,234.56"` -> BigDecimal("1234.56")
 * `123.45`     -> BigDecimal("123.45")
 *
 * Blank strings, "--", and JSON null are treated as missing values.
 *
 * This adapter belongs to the remote data boundary. The purpose is
 * to prevent API-specific String parsing from leaking into domain
 * or application logic.
 */
class BigDecimalTypeAdapter : TypeAdapter<BigDecimal?>() {

    override fun read(reader: JsonReader): BigDecimal? {

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        val rawValue = reader.nextString()
            .replace(",", "")
            .trim()

        if (
            rawValue.isEmpty() ||
            rawValue == "--"
        ) {
            return null
        }

        return rawValue.toBigDecimalOrNull()
    }

    override fun write(
        writer: JsonWriter,
        value: BigDecimal?
    ) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.value(value)
    }
}