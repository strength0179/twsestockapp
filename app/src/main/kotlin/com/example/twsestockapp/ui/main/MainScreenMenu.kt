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

/**
 * Main-screen overflow menu.
 *
 * Stock selection remains available through the bottom-right edit
 * button.
 *
 * This menu is reserved for Pager-field editing.
 */
@Composable
internal fun MainScreenMenu(
    onEditFields: () -> Unit,
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
                        "開啟主畫面選單"
                },
        ) {
            Text(
                text = "⋮",
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
            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            "編輯資料欄位",
                    )
                },
                onClick = {
                    expanded =
                        false

                    onEditFields()
                },
            )
        }
    }
}
