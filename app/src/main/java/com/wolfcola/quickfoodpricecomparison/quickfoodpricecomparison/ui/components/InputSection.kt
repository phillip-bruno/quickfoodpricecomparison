package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.ALL_CATEGORIES_LABEL
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme.FoodInfoColor
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme.InputBackground
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSection(viewModel: MainViewModel) {
    val currency by viewModel.currency.collectAsState()
    val price by viewModel.price.collectAsState()
    val selectedUnitIndex by viewModel.selectedUnitIndex.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredFoods by viewModel.filteredFoodItems.collectAsState()
    val selectedFoodIndex by viewModel.selectedFoodIndex.collectAsState()
    val comment by viewModel.comment.collectAsState()
    val foodInfo by viewModel.foodInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackground)
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
                value = currency,
                onValueChange = { viewModel.onCurrencyChanged(it) },
                modifier = Modifier.width(60.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = price,
                onValueChange = { viewModel.onPriceChanged(it) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // Unit selection dropdown
        DropdownSelector(
            label = "Unit",
            items = SELECTION_LIST.map { it.name },
            selectedIndex = selectedUnitIndex,
            onSelected = { viewModel.onUnitSelected(it) }
        )

        // Category filter dropdown
        DropdownSelector(
            label = "Category",
            items = listOf(ALL_CATEGORIES_LABEL) + viewModel.categories,
            selectedIndex = (listOf(ALL_CATEGORIES_LABEL) + viewModel.categories).indexOf(selectedCategory).coerceAtLeast(0),
            onSelected = { idx ->
                val cats = listOf(ALL_CATEGORIES_LABEL) + viewModel.categories
                viewModel.onCategoryChanged(cats[idx])
            }
        )

        // Food item dropdown
        DropdownSelector(
            label = "Food Item",
            items = filteredFoods.map { it.foodName },
            selectedIndex = selectedFoodIndex,
            onSelected = { viewModel.onFoodSelected(it) }
        )

        // Food info subtitle
        if (foodInfo.isNotEmpty()) {
            Text(
                text = foodInfo,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = FoodInfoColor
            )
        }

        // Comment
        OutlinedTextField(
            value = comment,
            onValueChange = { viewModel.onCommentChanged(it) },
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
