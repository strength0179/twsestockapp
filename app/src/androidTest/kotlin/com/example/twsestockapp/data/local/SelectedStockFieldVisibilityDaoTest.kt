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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests hidden-field persistence and visible-aware absolute ordering.
 */
@RunWith(AndroidJUnit4::class)
class SelectedStockFieldVisibilityDaoTest {

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
    fun hideKeepsRowAndAbsoluteOrder() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        assertRows(
            row("A", 1, true),
            row("B", 2, false),
            row("C", 3, true),
            row("D", 4, true),
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

    @Test
    fun showRestoresFieldAtOriginalAbsoluteOrder() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        dao.show(
            fieldKey = "B"
        )

        assertRows(
            row("A", 1, true),
            row("B", 2, true),
            row("C", 3, true),
            row("D", 4, true),
        )
    }

    /**
     * Required example:
     *
     * A1 B2(hidden) C3 D4
     *
     * moveForward(C)
     *
     * C1 A2 B3(hidden) D4
     */
    @Test
    fun moveForwardSkipsHiddenFieldButShiftsItsAbsoluteOrder() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        dao.moveForward(
            fieldKey = "C"
        )

        assertRows(
            row("C", 1, true),
            row("A", 2, true),
            row("B", 3, false),
            row("D", 4, true),
        )
    }

    /**
     * After the previous move, restoring B must reveal:
     *
     * C1 A2 B3 D4
     */
    @Test
    fun restoredFieldUsesShiftedAbsoluteOrder() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        dao.moveForward(
            fieldKey = "C"
        )

        dao.show(
            fieldKey = "B"
        )

        assertRows(
            row("C", 1, true),
            row("A", 2, true),
            row("B", 3, true),
            row("D", 4, true),
        )
    }

    /**
     * Moving toward the end also shifts hidden fields.
     *
     * A1 B2(hidden) C3 D4
     *
     * moveBackward(A)
     *
     * B1(hidden) C2 A3 D4
     */
    @Test
    fun moveBackwardSkipsHiddenFieldButShiftsItsAbsoluteOrder() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        dao.moveBackward(
            fieldKey = "A"
        )

        assertRows(
            row("B", 1, false),
            row("C", 2, true),
            row("A", 3, true),
            row("D", 4, true),
        )
    }

    @Test
    fun hiddenFieldCannotBeMovedDirectly() = runTest {
        installABCD()

        dao.hide(
            fieldKey = "B"
        )

        dao.moveForward(
            fieldKey = "B"
        )

        dao.moveBackward(
            fieldKey = "B"
        )

        assertRows(
            row("A", 1, true),
            row("B", 2, false),
            row("C", 3, true),
            row("D", 4, true),
        )
    }

    @Test
    fun finalVisibleFieldCannotBeHidden() = runTest {
        dao.replaceAll(
            listOf(
                entity(
                    fieldKey = "A",
                    isVisible = true,
                ),
                entity(
                    fieldKey = "B",
                    isVisible = false,
                ),
            )
        )

        dao.hide(
            fieldKey = "A"
        )

        assertRows(
            row("A", 1, true),
            row("B", 2, false),
        )
    }

    private suspend fun installABCD() {
        dao.replaceAll(
            listOf(
                entity("A"),
                entity("B"),
                entity("C"),
                entity("D"),
            )
        )
    }

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

    private fun entity(
        fieldKey: String,
        isVisible: Boolean = true,
    ): SelectedStockFieldEntity {
        return SelectedStockFieldEntity(
            fieldKey =
                fieldKey,
            isVisible =
                isVisible,
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
