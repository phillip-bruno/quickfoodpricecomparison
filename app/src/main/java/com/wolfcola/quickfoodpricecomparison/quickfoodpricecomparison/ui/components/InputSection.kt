package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme.LocalExtendedColors
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSection(
    state: MainUiState,
    onCurrencyChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onUnitSelected: (Int) -> Unit,
    onFoodSearchQueryChanged: (String) -> Unit,
    onFoodSelected: (FoodDensity) -> Unit,
    onCommentChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalExtendedColors.current.inputBackground)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Input", style = MaterialTheme.typography.titleMedium)

        // Cost input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cost:", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(
                value = state.currency,
                onValueChange = onCurrencyChanged,
                modifier = Modifier.width(60.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = state.price,
                onValueChange = onPriceChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = state.priceError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        state.priceError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Unit selection dropdown
        DropdownSelector(
            label = "Unit",
            items = SELECTION_LIST.map { it.name },
            selectedIndex = state.selectedUnitIndex,
            onSelected = onUnitSelected
        )

        // Food search field (replaces Category + Food Item dropdowns)
        FoodSearchField(
            query = state.foodSearchQuery,
            results = state.foodSearchResults,
            hasSelection = state.selectedFood != null,
            onQueryChanged = onFoodSearchQueryChanged,
            onFoodSelected = onFoodSelected
        )

        // Food info subtitle
        val foodInfo = state.foodInfo
        if (foodInfo.isNotEmpty()) {
            Text(
                text = foodInfo,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = LocalExtendedColors.current.foodInfo
            )
        }

        // Comment
        OutlinedTextField(
            value = state.comment,
            onValueChange = onCommentChanged,
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchField(
    query: String,
    results: List<FoodDensity>,
    hasSelection: Boolean,
    onQueryChanged: (String) -> Unit,
    onFoodSelected: (FoodDensity) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val expanded = query.isNotBlank() && results.isNotEmpty() && !hasSelection

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Food:", style = MaterialTheme.typography.bodyMedium)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {},
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    label = { Text("Search food or category") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                onQueryChanged("")
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                if (expanded) {
                    ExposedDropdownMenu(
                        expanded = true,
                        onDismissRequest = {}
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            results.forEach { food ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = food.foodName,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = food.category,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = LocalExtendedColors.current.foodInfo
                                            )
                                        }
                                    },
                                    onClick = {
                                        onFoodSelected(food)
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (query.isNotBlank() && results.isEmpty() && !hasSelection) {
            Text(
                text = "No matches",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = LocalExtendedColors.current.foodInfo,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = items.getOrElse(selectedIndex) { items.firstOrNull() ?: "" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("$label:", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
