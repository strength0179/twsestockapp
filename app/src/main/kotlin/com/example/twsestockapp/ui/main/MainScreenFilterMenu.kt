package com.example.twsestockapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.twsestockapp.ui.main.sort.StockSortOption

/**
 * Main-screen stock sorting menu.
 *
 * The menu only selects presentation sorting. Persistent stock
 * selection order is never modified here.
 */
@Composable
internal fun MainScreenFilterMenu(
    selectedOption: StockSortOption,
    onSelectOption:
        (StockSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by
        rememberSaveable {
            mutableStateOf(false)
        }

    Box(
        modifier =
            modifier,
    ) {
        IconButton(
            onClick = {
                expanded =
                    true
            },
            modifier =
                Modifier.semantics {
                    contentDescription =
                        "開啟股票排序選單"
                },
        ) {
            /*
             * Kept dependency-free intentionally.
             *
             * This can later be replaced by a Material Filter icon
             * without changing the menu contract.
             */
            Text(
                text = "≡",
            )
        }

        DropdownMenu(
            expanded =
                expanded,
            onDismissRequest = {
                expanded =
                    false
            },
        ) {
            StockSortOption.entries
                .forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text =
                                    option.label(),
                            )
                        },
                        trailingIcon = {
                            if (
                                option ==
                                selectedOption
                            ) {
                                Text(
                                    text = "✓",
                                )
                            }
                        },
                        onClick = {
                            expanded =
                                false

                            onSelectOption(
                                option
                            )
                        },
                        modifier =
                            Modifier.semantics {
                                contentDescription =
                                    sortOptionDescription(
                                        option =
                                            option,
                                        selected =
                                            option ==
                                                selectedOption,
                                    )
                            },
                    )
                }
        }
    }
}

/**
 * User-visible label for one stock sorting option.
 */
private fun StockSortOption.label():
    String {

    return when (this) {
        StockSortOption.Default ->
            "預設"

        StockSortOption.PeRatio ->
            "本益比"

        StockSortOption.DividendYield ->
            "殖利率(%)"

        StockSortOption.PbRatio ->
            "股價淨值比"
    }
}

private fun sortOptionDescription(
    option: StockSortOption,
    selected: Boolean,
): String {

    val label =
        option.label()

    return if (selected) {
        "股票排序 $label，已選取"
    } else {
        "股票排序 $label"
    }
}
