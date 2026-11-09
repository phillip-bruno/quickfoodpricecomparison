package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry

/** Raw numeric output of a conversion. Formatting (currency, notation) happens at display time. */
data class ConversionResult(
    val pricePerGram: Double,
    val prices: Map<ConversionUnit, Double>
)

data class MainUiState(
    val currency: String = "$",
    val price: String = "1",
    val priceError: String? = null,
    val selectedUnitIndex: Int = 0,
    val foodSearchQuery: String = "",
    val foodSearchResults: List<FoodDensity> = emptyList(),
    val selectedFood: FoodDensity? = null,
    val comment: String = "",
    val result: ConversionResult? = null,
    val history: List<HistoryEntry> = emptyList()
) {
    val showClearButton: Boolean get() = result != null

    val foodInfo: String
        get() = selectedFood?.let { food ->
            buildList {
                if (food.category.isNotEmpty()) add("Category: ${food.category}")
                add("Density: ${food.gMl} g/ml")
                if (food.biblioId.isNotEmpty()) add("Source: ${food.biblioId}")
            }.joinToString(" | ")
        } ?: ""
}
