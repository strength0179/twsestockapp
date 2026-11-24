package com.example.twsestockapp.ui.main.component

import com.example.twsestockapp.data.selection.StockFieldSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM tests for [resolveStockPagerPages].
 *
 * Required valuation fields:
 *
 * - peRatio
 * - dividendYield
 * - pbRatio
 *
 * These fields are always effectively visible regardless of persisted
 * IsVisible.
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
    fun selectedFieldsAreReturned() {
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

    @Test
    fun optionalHiddenFieldIsExcludedWithoutRenumberingPersistentOrder() {
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

    @Test
    fun optionalHiddenFieldAbsoluteOrderStillInfluencesMovedVisiblePages() {
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

    @Test
    fun restoredOptionalFieldReturnsAtPersistentAbsolutePosition() {
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
    fun allOptionalHiddenFieldsReturnNoPagerPages() {
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

    /**
     * Core invariant:
     *
     * Mandatory valuation pages are returned even when persistence says
     * IsVisible=false.
     */
    @Test
    fun persistedHiddenRequiredValuationFieldsRemainInPager() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "peRatio",
                            1,
                            isVisible =
                                false,
                        ),
                        field(
                            "dividendYield",
                            2,
                            isVisible =
                                false,
                        ),
                        field(
                            "pbRatio",
                            3,
                            isVisible =
                                false,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "peRatio",
            "dividendYield",
            "pbRatio",
        )
    }

    @Test
    fun requiredValuationFieldsUsePersistentDisplayOrderWhenForcedVisible() {
        val pages =
            resolveStockPagerPages(
                selections =
                    listOf(
                        field(
                            "pbRatio",
                            3,
                            isVisible =
                                false,
                        ),
                        field(
                            "peRatio",
                            1,
                            isVisible =
                                false,
                        ),
                        field(
                            "dividendYield",
                            2,
                            isVisible =
                                false,
                        ),
                    ),
            )

        assertKeys(
            pages,
            "peRatio",
            "dividendYield",
            "pbRatio",
        )
    }

    @Test
    fun requiredAndOptionalVisibilityRulesCanCoexist() {
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
                            "peRatio",
                            2,
                            isVisible =
                                false,
                        ),
                        field(
                            "change",
                            3,
                            isVisible =
                                true,
                        ),
                        field(
                            "dividendYield",
                            4,
                            isVisible =
                                false,
                        ),
                        field(
                            "tradeVolume",
                            5,
                            isVisible =
                                false,
                        ),
                        field(
                            "pbRatio",
                            6,
                            isVisible =
                                false,
                        ),
                    ),
            )

        /*
         * closingPrice and tradeVolume remain hidden.
         *
         * All mandatory valuation fields are present.
         */
        assertKeys(
            pages,
            "peRatio",
            "change",
            "dividendYield",
            "pbRatio",
        )
    }

    @Test
    fun resolvingRequiredVisibilityDoesNotMutatePersistedSelections() {
        val selections =
            listOf(
                field(
                    "peRatio",
                    1,
                    isVisible =
                        false,
                ),
                field(
                    "dividendYield",
                    2,
                    isVisible =
                        false,
                ),
                field(
                    "pbRatio",
                    3,
                    isVisible =
                        false,
                ),
            )

        val pages =
            resolveStockPagerPages(
                selections =
                    selections,
            )

        assertEquals(
            3,
            pages.size,
        )

        assertFalse(
            selections[0].IsVisible
        )

        assertFalse(
            selections[1].IsVisible
        )

        assertFalse(
            selections[2].IsVisible
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
