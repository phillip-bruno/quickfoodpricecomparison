package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme.LocalExtendedColors
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainViewModel

@Composable
fun HistorySection(viewModel: MainViewModel) {
    val entries by viewModel.historyEntries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("History", style = MaterialTheme.typography.titleMedium)

        if (entries.isEmpty()) {
            Text(
                "No conversions yet. Press Convert to get started.",
                fontStyle = FontStyle.Italic,
                color = LocalExtendedColors.current.emptyState,
                modifier = Modifier.padding(10.dp)
            )
        } else {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                entries.forEachIndexed { index, entry ->
                    HistoryRow(entry = entry, onClick = { viewModel.onHistoryItemSelected(entry) })
                    if (index < entries.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = entry.unitSelection ?: "",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.unitValue ?: "",
            fontSize = 13.sp,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = entry.densitySelection ?: "",
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.comment ?: "",
            fontSize = 13.sp,
            modifier = Modifier.weight(0.8f)
        )
    }
}
