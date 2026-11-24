package com.example.twsestockapp.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class BigDecimalTypeAdapterTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(
            BigDecimal::class.java,
            BigDecimalTypeAdapter()
        )
        .create()

    @Test
    fun quotedDecimalStringIsConvertedToBigDecimal() {
        val result = gson.fromJson(
            "\"123.45\"",
            BigDecimal::class.java
        )

        assertEquals(
            BigDecimal("123.45"),
            result
        )
    }

    @Test
    fun commaSeparatedDecimalStringIsConvertedToBigDecimal() {
        val result = gson.fromJson(
            "\"1,234.56\"",
            BigDecimal::class.java
        )

        assertEquals(
            BigDecimal("1234.56"),
            result
        )
    }

    @Test
    fun surroundingWhitespaceIsRemovedBeforeConversion() {
        val result = gson.fromJson(
            "\"  123.45  \"",
            BigDecimal::class.java
        )

        assertEquals(
            BigDecimal("123.45"),
            result
        )
    }

    @Test
    fun jsonNumberIsConvertedToBigDecimal() {
        val result = gson.fromJson(
            "123.45",
            BigDecimal::class.java
        )

        assertEquals(
            BigDecimal("123.45"),
            result
        )
    }

    @Test
    fun negativeValueIsConvertedToBigDecimal() {
        val result = gson.fromJson(
            "\"-12.50\"",
            BigDecimal::class.java
        )

        assertEquals(
            BigDecimal("-12.50"),
            result
        )
    }

    @Test
    fun blankStringReturnsNull() {
        val result = gson.fromJson(
            "\"\"",
            BigDecimal::class.java
        )

        assertNull(result)
    }

    @Test
    fun dashPlaceholderReturnsNull() {
        val result = gson.fromJson(
            "\"--\"",
            BigDecimal::class.java
        )

        assertNull(result)
    }

    @Test
    fun invalidStringReturnsNull() {
        val result = gson.fromJson(
            "\"not-a-number\"",
            BigDecimal::class.java
        )

        assertNull(result)
    }

    @Test
    fun jsonNullReturnsNull() {
        val result = gson.fromJson(
            "null",
            BigDecimal::class.java
        )

        assertNull(result)
    }

    @Test
    fun bigDecimalIsWrittenAsJsonNumber() {
        val result = gson.toJson(
            BigDecimal("123.45"),
            BigDecimal::class.java
        )

        assertEquals(
            "123.45",
            result
        )
    }
}