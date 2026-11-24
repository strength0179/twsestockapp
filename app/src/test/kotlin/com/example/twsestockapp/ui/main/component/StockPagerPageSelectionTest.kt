package com.example.twsestockapp.ui.main.component

import com.example.twsestockapp.data.selection.StockFieldSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM tests for [resolveStockPagerPages].
 */
class StockPagerPageSelectionTest {

    @Test
    fun selectionsDeterminePagerOrderByDisplayOrder() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "peRatio",
                            3,
                        ),
                        field(
                            "closingPrice",
                            1,
                        ),
                        field(
                            "tradeVolume",
                            2,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "closingPrice",
            "tradeVolume",
            "peRatio",
        )
    }

    @Test
    fun onlySelectedFieldsAreReturned() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "closingPrice",
                            1,
                        ),
                        field(
                            "dividendYield",
                            2,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "closingPrice",
            "dividendYield",
        )
    }

    @Test
    fun unknownPersistedFieldKeysAreIgnored() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "closingPrice",
                            1,
                        ),
                        field(
                            "fieldThatNoLongerExists",
                            2,
                        ),
                        field(
                            "tradeVolume",
                            3,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "closingPrice",
            "tradeVolume",
        )
    }

    @Test
    fun emptySelectionsReturnNoPagerPages() {
        val pages =
            resolveStockPagerPages(
                selections =
                    emptyList(),
            )

        assertTrue(
            pages.isEmpty()
        )
    }

    @Test
    fun inputListOrderDoesNotOverrideDisplayOrder() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "dividendYield",
                            4,
                        ),
                        field(
                            "change",
                            2,
                        ),
                        field(
                            "pbRatio",
                            3,
                        ),
                        field(
                            "monthlyAveragePrice",
                            1,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "monthlyAveragePrice",
            "change",
            "pbRatio",
            "dividendYield",
        )
    }

    /**
     * Absolute persistent order:
     *
     * closingPrice 1 visible
     * change       2 hidden
     * tradeVolume  3 visible
     * peRatio      4 visible
     *
     * Pager:
     *
     * closingPrice
     * tradeVolume
     * peRatio
     */
    @Test
    fun hiddenFieldIsExcludedWithoutRenumberingPersistentOrder() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "closingPrice",
                            1,
                        ),
                        field(
                            "change",
                            2,
                            isVisible =
                                false,
                        ),
                        field(
                            "tradeVolume",
                            3,
                        ),
                        field(
                            "peRatio",
                            4,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "closingPrice",
            "tradeVolume",
            "peRatio",
        )
    }

    /**
     * This represents the DAO result after:
     *
     * A1 B2(hidden) C3 D4
     *
     * moveForward(C)
     *
     * C1 A2 B3(hidden) D4
     *
     * Pager must therefore become:
     *
     * C A D
     */
    @Test
    fun hiddenFieldAbsoluteOrderStillInfluencesMovedVisiblePages() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "tradeVolume",
                            1,
                        ),
                        field(
                            "closingPrice",
                            2,
                        ),
                        field(
                            "change",
                            3,
                            isVisible =
                                false,
                        ),
                        field(
                            "peRatio",
                            4,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "tradeVolume",
            "closingPrice",
            "peRatio",
        )
    }

    /**
     * Restoring the hidden B at its preserved shifted absolute position
     * immediately places it back between A and D.
     *
     * Absolute order:
     *
     * C1 A2 B3 D4
     */
    @Test
    fun restoredFieldReturnsAtItsPersistentAbsolutePosition() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "tradeVolume",
                            1,
                        ),
                        field(
                            "closingPrice",
                            2,
                        ),
                        field(
                            "change",
                            3,
                            isVisible =
                                true,
                        ),
                        field(
                            "peRatio",
                            4,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "tradeVolume",
            "closingPrice",
            "change",
            "peRatio",
        )
    }

    @Test
    fun allHiddenFieldsReturnNoPagerPages() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "closingPrice",
                            1,
                            isVisible =
                                false,
                        ),
                        field(
                            "change",
                            2,
                            isVisible =
                                false,
                        ),
                    ),
            )

        assertTrue(
            pages.isEmpty()
        )
    }

    private fun field(
        fieldKey: String,
        displayOrder: Int,
        isVisible: Boolean = true,
    ): StockFieldSelection {

        return StockFieldSelection(
            FieldKey =
                fieldKey,
            DisplayOrder =
                displayOrder,
            IsVisible =
                isVisible,
        )
    }

    private fun assertKeys(
        actual:
            List<StockPagerPage>,
        vararg expected:
            String,
    ) {
        assertEquals(
            expected.toList(),
            actual.map { page ->
                page.key
            },
        )
    }
}
