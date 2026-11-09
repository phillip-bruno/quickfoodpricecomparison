package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.HistorySection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.InputSection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.PricePerUnitDisplay
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.ResultsSection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainViewModel

/**
 * The only composable that knows about MainViewModel: it collects the single UI state and hands
 * plain values and callbacks to the stateless sections below.
 */
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val bytes = viewModel.getHistoryBytes()
            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null) viewModel.importHistory(bytes)
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                onClearInput = viewModel::resetInput,
                onClearHistory = viewModel::clearHistory,
                onExportHistory = {
                    val epoch = System.currentTimeMillis() / 1000
                    exportLauncher.launch("quick_food_price_comparison_$epoch.json")
                },
                onImportHistory = { importLauncher.launch("*/*") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            InputSection(
                state = state,
                onCurrencyChanged = viewModel::onCurrencyChanged,
                onPriceChanged = viewModel::onPriceChanged,
                onUnitSelected = viewModel::onUnitSelected,
                onFoodSearchQueryChanged = viewModel::onFoodSearchQueryChanged,
                onFoodSelected = viewModel::onFoodItemDirectlySelected,
                onCommentChanged = viewModel::onCommentChanged
            )
            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Button(
                    onClick = viewModel::performConversion,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Convert")
                }
                if (state.showClearButton) {
                    OutlinedButton(
                        onClick = viewModel::resetInput,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Clear")
                    }
                }
            }

            HorizontalDivider()
            state.result?.let { result ->
                PricePerUnitDisplay(currency = state.currency, pricePerGram = result.pricePerGram)
                HorizontalDivider()
                ResultsSection(currency = state.currency, prices = result.prices)
                HorizontalDivider()
            }
            HistorySection(entries = state.history, onEntryClick = viewModel::onHistoryItemSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    onClearInput: () -> Unit,
    onClearHistory: () -> Unit,
    onExportHistory: () -> Unit,
    onImportHistory: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    @Composable
    fun MenuItem(label: String, onClick: () -> Unit) = DropdownMenuItem(
        text = { Text(label) },
        onClick = {
            onClick()
            menuExpanded = false
        }
    )

    TopAppBar(
        title = { Text("Food Price Comparison") },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                MenuItem("Clear Input", onClearInput)
                MenuItem("Clear History", onClearHistory)
                MenuItem("Export History", onExportHistory)
                MenuItem("Import History", onImportHistory)
            }
        }
    )
}
