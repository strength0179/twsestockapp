package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock

/**
 * Temporary renderer for a single [Stock].
 *
 * This will later be replaced by the formal stock card component.
 */
@Composable
internal fun StockGreeting(
    stock: Stock,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${stock.Code} ${stock.Name}",
        modifier = modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        style = MaterialTheme.typography.bodyLarge
    )
}