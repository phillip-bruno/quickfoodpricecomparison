package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceConverter
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.ALL_CATEGORIES_LABEL
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensityRepository
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodDensityRepository(application)
    private val historyManager = HistoryManager(application)

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

    private val _selectedCategory = MutableStateFlow(ALL_CATEGORIES_LABEL)
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment

    // Filtered food items based on category
    val filteredFoodItems: StateFlow<List<FoodDensity>> = _selectedCategory
        .combine(MutableStateFlow(allFoodItems)) { category, items ->
            if (category == ALL_CATEGORIES_LABEL) items
            else items.filter { it.category == category }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, allFoodItems)

    private val _selectedFoodIndex = MutableStateFlow(0)
    val selectedFoodIndex: StateFlow<Int> = _selectedFoodIndex

    // Results
    private val _results = MutableStateFlow<Map<String, Double>>(emptyMap())
    val results: StateFlow<Map<String, Double>> = _results

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

    fun onCategoryChanged(category: String) {
        _selectedCategory.value = category
        _selectedFoodIndex.value = 0
        updateFoodInfo()
    }

    fun onFoodSelected(index: Int) {
        _selectedFoodIndex.value = index
        updateFoodInfo()
    }

    fun onCommentChanged(value: String) {
        _comment.value = value
    }

    fun getSelectedFood(): FoodDensity? {
        val items = filteredFoodItems.value
        val idx = _selectedFoodIndex.value
        return items.getOrNull(idx)
    }

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
        val ppu = (priceValue / selectionItem.massInGrams * 100000.0).toLong() / 100000.0
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
        _selectedCategory.value = ALL_CATEGORIES_LABEL
        _selectedFoodIndex.value = 0
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

        // Reset category to show all items
        _selectedCategory.value = ALL_CATEGORIES_LABEL
        val foodIndex = allFoodItems.indexOfFirst { it.foodName == entry.densitySelection }
        if (foodIndex >= 0) _selectedFoodIndex.value = foodIndex

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
