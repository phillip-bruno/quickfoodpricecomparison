package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceConverter
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensityRepository
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensitySource
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryManager
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * repository/historyManager default to the real, Context-backed implementations so existing
 * call sites (viewModel() in Compose) are unaffected; tests can inject fakes instead.
 */
class MainViewModel(
    application: Application,
    private val repository: FoodDensitySource = FoodDensityRepository(application),
    private val historyManager: HistoryStore = HistoryManager(application)
) : AndroidViewModel(application) {

    // Data
    val allFoodItems: List<FoodDensity> = repository.loadFoodDensities()
    val categories: List<String> = FoodDensityRepository.getFoodCategories(allFoodItems)

    // Input state
    private val _currency = MutableStateFlow("$")
    val currency: StateFlow<String> = _currency

    private val _price = MutableStateFlow("1")
    val price: StateFlow<String> = _price

    private val _selectedUnitIndex = MutableStateFlow(0)
    val selectedUnitIndex: StateFlow<Int> = _selectedUnitIndex

    private val _foodSearchQuery = MutableStateFlow("")
    val foodSearchQuery: StateFlow<String> = _foodSearchQuery

    private val _selectedFood = MutableStateFlow<FoodDensity?>(null)
    val selectedFood: StateFlow<FoodDensity?> = _selectedFood

    val foodSearchResults: StateFlow<List<FoodDensity>> = _foodSearchQuery
        .combine(MutableStateFlow(allFoodItems)) { query, items ->
            if (query.isBlank()) emptyList()
            else {
                val q = query.trim().lowercase()
                items.filter { it.foodName.lowercase().contains(q) || it.category.lowercase().contains(q) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment

    // Results
    private val _results = MutableStateFlow<Map<ConversionUnit, Double>>(emptyMap())
    val results: StateFlow<Map<ConversionUnit, Double>> = _results

    private val _pricePerUnit = MutableStateFlow("")
    val pricePerUnit: StateFlow<String> = _pricePerUnit

    private val _showClearButton = MutableStateFlow(false)
    val showClearButton: StateFlow<Boolean> = _showClearButton

    // History
    private val _historyEntries = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val historyEntries: StateFlow<List<HistoryEntry>> = _historyEntries

    // Food info
    private val _foodInfo = MutableStateFlow("")
    val foodInfo: StateFlow<String> = _foodInfo

    init {
        _historyEntries.value = historyManager.loadHistory()
        updateFoodInfo()
    }

    fun onCurrencyChanged(value: String) {
        _currency.value = value
    }

    fun onPriceChanged(value: String) {
        _price.value = value
    }

    fun onUnitSelected(index: Int) {
        _selectedUnitIndex.value = index
    }

    fun onFoodSearchQueryChanged(query: String) {
        _foodSearchQuery.value = query
        if (_selectedFood.value != null) _selectedFood.value = null
        updateFoodInfo()
    }

    fun onFoodItemDirectlySelected(food: FoodDensity) {
        _selectedFood.value = food
        _foodSearchQuery.value = food.foodName
        updateFoodInfo()
    }

    fun onCommentChanged(value: String) {
        _comment.value = value
    }

    fun getSelectedFood(): FoodDensity? = _selectedFood.value

    private fun updateFoodInfo() {
        val food = getSelectedFood()
        if (food != null) {
            val parts = mutableListOf<String>()
            if (food.category.isNotEmpty()) parts.add("Category: ${food.category}")
            parts.add("Density: ${food.gMl} g/ml")
            if (food.biblioId.isNotEmpty()) parts.add("Source: ${food.biblioId}")
            _foodInfo.value = parts.joinToString(" | ")
        } else {
            _foodInfo.value = ""
        }
    }

    fun performConversion() {
        val priceValue = _price.value.toDoubleOrNull() ?: return
        val food = getSelectedFood() ?: return
        val unitIndex = _selectedUnitIndex.value
        val selectionItem = SELECTION_LIST.getOrNull(unitIndex) ?: return

        saveCurrentToHistory(food, selectionItem.name)

        val currency = _currency.value
        val results = PriceConverter.convertAllPrices(
            price = priceValue,
            sourceMassInGrams = selectionItem.massInGrams,
            densityGPerMl = food.gMl,
            units = CONVERSION_UNITS
        )
        val ppu = PriceConverter.round5(priceValue / selectionItem.massInGrams)
        _pricePerUnit.value = "$currency$ppu"
        _results.value = results
        _showClearButton.value = true
    }

    private fun saveCurrentToHistory(food: FoodDensity, unitName: String) {
        val entries = HistoryManager.cleanupHistory(
            _historyEntries.value,
            currentDensity = food.foodName,
            currentUnit = unitName,
            currentValue = _price.value
        ).toMutableList()
        entries.add(
            HistoryEntry(
                unitSelection = unitName,
                unitValue = _price.value,
                densitySelection = food.foodName,
                comment = _comment.value
            )
        )
        _historyEntries.value = entries
        historyManager.saveHistory(entries)
    }

    fun resetInput() {
        _price.value = "1"
        _selectedUnitIndex.value = 0
        _foodSearchQuery.value = ""
        _selectedFood.value = null
        _comment.value = ""
        _results.value = emptyMap()
        _pricePerUnit.value = ""
        _showClearButton.value = false
        updateFoodInfo()
    }

    fun clearHistory() {
        historyManager.clearHistory()
        _historyEntries.value = emptyList()
    }

    fun onHistoryItemSelected(entry: HistoryEntry) {
        if (entry.unitSelection == null) return

        _price.value = entry.unitValue ?: "1"

        val unitIndex = SELECTION_LIST.indexOfFirst { it.name == entry.unitSelection }
        if (unitIndex >= 0) _selectedUnitIndex.value = unitIndex

        val food = allFoodItems.firstOrNull { it.foodName == entry.densitySelection }
        _selectedFood.value = food
        _foodSearchQuery.value = food?.foodName ?: ""

        _comment.value = entry.comment ?: ""
        updateFoodInfo()
        performConversion()
    }

    fun getHistoryBytes(): ByteArray {
        return historyManager.historyToBytes(_historyEntries.value)
    }

    fun importHistory(bytes: ByteArray) {
        try {
            val imported = HistoryManager.parseJsonEntries(String(bytes, Charsets.UTF_8))
            val current = _historyEntries.value.toMutableList()
            current.addAll(imported)
            _historyEntries.value = current
            historyManager.saveHistory(current)
        } catch (_: Exception) {
            // Invalid import data
        }
    }
}
