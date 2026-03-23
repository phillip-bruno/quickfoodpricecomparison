package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.HistorySection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.InputSection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.PricePerUnitDisplay
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components.ResultsSection
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val showClear by viewModel.showClearButton.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
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
                        DropdownMenuItem(
                            text = { Text("Clear Input") },
                            onClick = {
                                viewModel.resetInput()
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear History") },
                            onClick = {
                                viewModel.clearHistory()
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export History") },
                            onClick = {
                                val epoch = System.currentTimeMillis() / 1000
                                exportLauncher.launch("quick_food_price_comparison_$epoch.json")
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Import History") },
                            onClick = {
                                importLauncher.launch("*/*")
                                menuExpanded = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            InputSection(viewModel)
            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Button(
                    onClick = { viewModel.performConversion() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Convert")
                }
                if (showClear) {
                    OutlinedButton(
                        onClick = { viewModel.resetInput() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Clear")
                    }
                }
            }

            HorizontalDivider()
            PricePerUnitDisplay(viewModel)
            HorizontalDivider()
            ResultsSection(viewModel)
            HorizontalDivider()
            HistorySection(viewModel)
        }
    }
}
