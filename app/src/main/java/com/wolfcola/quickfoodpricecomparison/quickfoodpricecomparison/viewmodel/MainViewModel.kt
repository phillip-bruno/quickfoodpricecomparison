package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceConverter
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceInput
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensityRepository
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensitySource
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryManager
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The three-argument constructor lets tests inject fakes for the Context-backed dependencies.
 *
 * Production code goes through the single-argument constructor, which AndroidViewModelFactory
 * looks up reflectively when Compose calls viewModel(). Kotlin default arguments do NOT emit
 * that one-argument JVM constructor, so it must be declared explicitly or the app crashes at
 * startup with NoSuchMethodException; MainViewModelTest guards this.
 */
class MainViewModel(
    application: Application,
    repository: FoodDensitySource,
    private val historyManager: HistoryStore
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        FoodDensityRepository(application),
        HistoryManager(application)
    )

    private val allFoodItems: List<FoodDensity> = repository.loadFoodDensities()

    private val _uiState = MutableStateFlow(MainUiState(history = historyManager.loadHistory()))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onCurrencyChanged(value: String) = _uiState.update { it.copy(currency = value) }

    fun onPriceChanged(value: String) = _uiState.update { it.copy(price = value, priceError = null) }

    fun onUnitSelected(index: Int) = _uiState.update { it.copy(selectedUnitIndex = index) }

    fun onCommentChanged(value: String) = _uiState.update { it.copy(comment = value) }

    fun onFoodSearchQueryChanged(query: String) = _uiState.update {
        it.copy(foodSearchQuery = query, foodSearchResults = searchFoods(query), selectedFood = null)
    }

    fun onFoodItemDirectlySelected(food: FoodDensity) = _uiState.update {
        it.copy(selectedFood = food, foodSearchQuery = food.foodName, foodSearchResults = emptyList())
    }

    private fun searchFoods(query: String): List<FoodDensity> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return allFoodItems.filter { it.foodName.lowercase().contains(q) || it.category.lowercase().contains(q) }
    }

    /**
     * Validates first and only records history once a conversion has actually succeeded, so
     * bad input (e.g. a negative price) can neither crash the converter nor end up in history.
     */
    fun performConversion() {
        val state = _uiState.value
        val food = state.selectedFood ?: return
        val selection = SELECTION_LIST.getOrNull(state.selectedUnitIndex) ?: return

        val price = when (val input = PriceInput.parse(state.price)) {
            is PriceInput.Invalid -> {
                _uiState.update { it.copy(priceError = input.message) }
                return
            }
            is PriceInput.Valid -> input.value
        }

        val result = ConversionResult(
            pricePerGram = PriceConverter.round5(price / selection.massInGrams),
            prices = PriceConverter.convertAllPrices(
                price = price,
                sourceMassInGrams = selection.massInGrams,
                densityGPerMl = food.gMl,
                units = CONVERSION_UNITS
            )
        )
        val history = HistoryManager.cleanupHistory(
            state.history,
            currentDensity = food.foodName,
            currentUnit = selection.name,
            currentValue = state.price
        ) + HistoryEntry(
            unitSelection = selection.name,
            unitValue = state.price,
            densitySelection = food.foodName,
            comment = state.comment
        )

        _uiState.update { it.copy(result = result, priceError = null, history = history) }
        historyManager.saveHistory(history)
    }

    /** Clears the inputs and results; the chosen currency and history are kept. */
    fun resetInput() = _uiState.update { MainUiState(currency = it.currency, history = it.history) }

    fun clearHistory() {
        historyManager.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun onHistoryItemSelected(entry: HistoryEntry) {
        if (entry.unitSelection == null) return
        val food = allFoodItems.firstOrNull { it.foodName == entry.densitySelection }
        val unitIndex = SELECTION_LIST.indexOfFirst { it.name == entry.unitSelection }

        _uiState.update {
            it.copy(
                price = entry.unitValue ?: "1",
                priceError = null,
                selectedUnitIndex = if (unitIndex >= 0) unitIndex else it.selectedUnitIndex,
                selectedFood = food,
                foodSearchQuery = food?.foodName ?: "",
                foodSearchResults = emptyList(),
                comment = entry.comment ?: ""
            )
        }
        performConversion()
    }

    fun getHistoryBytes(): ByteArray = historyManager.historyToBytes(_uiState.value.history)

    fun importHistory(bytes: ByteArray) {
        try {
            val imported = HistoryManager.parseJsonEntries(String(bytes, Charsets.UTF_8))
            val merged = _uiState.value.history + imported
            _uiState.update { it.copy(history = merged) }
            historyManager.saveHistory(merged)
        } catch (_: Exception) {
            // Invalid import data
        }
    }
}
