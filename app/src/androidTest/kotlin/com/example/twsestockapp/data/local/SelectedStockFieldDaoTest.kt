package com.example.twsestockapp.data.local

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.twsestockapp.data.local.dao.SelectedStockFieldDao
import com.example.twsestockapp.data.local.database.TWSEStockDatabase
import com.example.twsestockapp.data.local.entity.SelectedStockFieldEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [SelectedStockFieldDao].
 *
 * Each test uses an isolated in-memory Room database.
 *
 * Visibility model:
 *
 * - every field remains persisted when hidden
 * - displayOrder is the absolute persistent order
 * - isVisible determines whether the field appears in the Pager
 * - hide/show do not directly change displayOrder
 */
@RunWith(AndroidJUnit4::class)
class SelectedStockFieldDaoTest {

    private lateinit var database:
        TWSEStockDatabase

    private lateinit var dao:
        SelectedStockFieldDao

    @Before
    fun createDatabase() {
        val context =
            ApplicationProvider
                .getApplicationContext<Context>()

        database =
            Room
                .inMemoryDatabaseBuilder<TWSEStockDatabase>(
                    context
                )
                .setDriver(
                    BundledSQLiteDriver()
                )
                .build()

        dao =
            database.selectedStockFieldDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun databaseInitiallyContainsNoSelections() = runTest {
        val selections =
            dao.observeSelections()
                .first()

        assertTrue(
            selections.isEmpty()
        )

        assertEquals(
            0,
            dao.count(),
        )

        assertEquals(
            0,
            dao.countVisible(),
        )
    }

    /**
     * Missing fields are appended in selection order and start visible.
     */
    @Test
    fun selectingFieldsAppendsThemInOrder() = runTest {
        select(
            "closingPrice"
        )

        select(
            "tradeVolume"
        )

        select(
            "peRatio"
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "tradeVolume",
                2,
                true,
            ),
            row(
                "peRatio",
                3,
                true,
            ),
        )
    }

    /**
     * Selecting an already-visible field does not create another row
     * or modify its absolute position.
     */
    @Test
    fun duplicateSelectionDoesNotCreateOrReorderRows() = runTest {
        select(
            "closingPrice"
        )

        select(
            "tradeVolume"
        )

        select(
            "closingPrice"
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "tradeVolume",
                2,
                true,
            ),
        )

        assertEquals(
            2,
            dao.count(),
        )

        assertEquals(
            2,
            dao.countVisible(),
        )
    }

    /**
     * unselect now means hide.
     *
     * The field remains persisted and all absolute displayOrder values
     * remain unchanged.
     */
    @Test
    fun unselectHidesFieldAndPreservesAbsoluteOrders() = runTest {
        select(
            "closingPrice"
        )

        select(
            "change"
        )

        select(
            "tradeVolume"
        )

        select(
            "peRatio"
        )

        dao.unselect(
            fieldKey =
                "change",
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "change",
                2,
                false,
            ),
            row(
                "tradeVolume",
                3,
                true,
            ),
            row(
                "peRatio",
                4,
                true,
            ),
        )

        assertEquals(
            4,
            dao.count(),
        )

        assertEquals(
            3,
            dao.countVisible(),
        )
    }

    /**
     * Selecting a hidden existing field restores visibility at the same
     * absolute position.
     */
    @Test
    fun selectingHiddenFieldRestoresOriginalAbsoluteOrder() = runTest {
        select(
            "closingPrice"
        )

        select(
            "change"
        )

        select(
            "tradeVolume"
        )

        dao.unselect(
            fieldKey =
                "change",
        )

        val hidden =
            findSelection(
                fieldKey =
                    "change",
            )

        assertFalse(
            hidden.isVisible
        )

        assertEquals(
            2,
            hidden.displayOrder,
        )

        select(
            "change"
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "change",
                2,
                true,
            ),
            row(
                "tradeVolume",
                3,
                true,
            ),
        )
    }

    /**
     * With no hidden fields between the visible rows, moveForward still
     * produces the familiar adjacent ordering behavior.
     */
    @Test
    fun moveForwardMovesBeforePreviousVisibleField() = runTest {
        select(
            "closingPrice"
        )

        select(
            "tradeVolume"
        )

        select(
            "peRatio"
        )

        dao.moveForward(
            fieldKey =
                "peRatio",
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "peRatio",
                2,
                true,
            ),
            row(
                "tradeVolume",
                3,
                true,
            ),
        )

        /*
         * First visible field cannot move farther forward.
         */
        dao.moveForward(
            fieldKey =
                "closingPrice",
        )

        assertRows(
            row(
                "closingPrice",
                1,
                true,
            ),
            row(
                "peRatio",
                2,
                true,
            ),
            row(
                "tradeVolume",
                3,
                true,
            ),
        )
    }

    /**
     * With no hidden fields between the visible rows, moveBackward
     * still produces the familiar adjacent ordering behavior.
     */
    @Test
    fun moveBackwardMovesAfterNextVisibleField() = runTest {
        select(
            "closingPrice"
        )

        select(
            "tradeVolume"
        )

        select(
            "peRatio"
        )

        dao.moveBackward(
            fieldKey =
                "closingPrice",
        )

        assertRows(
            row(
                "tradeVolume",
                1,
                true,
            ),
            row(
                "closingPrice",
                2,
                true,
            ),
            row(
                "peRatio",
                3,
                true,
            ),
        )

        /*
         * Final visible field cannot move farther backward.
         */
        dao.moveBackward(
            fieldKey =
                "peRatio",
        )

        assertRows(
            row(
                "tradeVolume",
                1,
                true,
            ),
            row(
                "closingPrice",
                2,
                true,
            ),
            row(
                "peRatio",
                3,
                true,
            ),
        )
    }

    /**
     * replaceAll reconstructs absolute order from List order.
     *
     * Visibility supplied by each entity is preserved.
     */
    @Test
    fun replaceAllUsesListOrderAndPreservesVisibility() = runTest {
        select(
            "closingPrice"
        )

        select(
            "change"
        )

        select(
            "tradeVolume"
        )

        dao.replaceAll(
            listOf(
                SelectedStockFieldEntity(
                    fieldKey =
                        "dividendYield",
                    displayOrder =
                        99,
                    isVisible =
                        true,
                ),
                SelectedStockFieldEntity(
                    fieldKey =
                        "peRatio",
                    displayOrder =
                        50,
                    isVisible =
                        false,
                ),
                SelectedStockFieldEntity(
                    fieldKey =
                        "closingPrice",
                    displayOrder =
                        10,
                    isVisible =
                        true,
                ),
            )
        )

        assertRows(
            row(
                "dividendYield",
                1,
                true,
            ),
            row(
                "peRatio",
                2,
                false,
            ),
            row(
                "closingPrice",
                3,
                true,
            ),
        )

        assertEquals(
            3,
            dao.count(),
        )

        assertEquals(
            2,
            dao.countVisible(),
        )
    }

    /**
     * Adds or restores one Pager field.
     */
    private suspend fun select(
        fieldKey: String,
    ) {
        dao.select(
            SelectedStockFieldEntity(
                fieldKey =
                    fieldKey,
                isVisible =
                    true,
            )
        )
    }

    /**
     * Returns one persisted field from the complete field collection.
     */
    private suspend fun findSelection(
        fieldKey: String,
    ): SelectedStockFieldEntity {

        return dao.observeSelections()
            .first()
            .first { selection ->
                selection.fieldKey ==
                    fieldKey
            }
    }

    /**
     * Validates absolute persistent order and visibility together.
     */
    private suspend fun assertRows(
        vararg expected:
            SelectedStockFieldEntity,
    ) {
        val actual =
            dao.observeSelections()
                .first()

        assertEquals(
            expected.toList(),
            actual,
        )
    }

    private fun row(
        fieldKey: String,
        displayOrder: Int,
        isVisible: Boolean,
    ): SelectedStockFieldEntity {

        return SelectedStockFieldEntity(
            fieldKey =
                fieldKey,
            displayOrder =
                displayOrder,
            isVisible =
                isVisible,
        )
    }
}
