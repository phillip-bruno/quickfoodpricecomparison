package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceConverter
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.PriceInput
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensityRepository
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensitySource
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.SELECTION_LIST
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryRules
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.ContentResolverDocuments
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.DocumentAccess
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryCodec
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryManager
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The multi-argument constructor lets tests inject fakes for the Context-backed dependencies.
 *
 * Production code goes through the single-argument constructor, which AndroidViewModelFactory
 * looks up reflectively when Compose calls viewModel(). Kotlin default arguments do NOT emit
 * that one-argument JVM constructor, so it must be declared explicitly or the app crashes at
 * startup with NoSuchMethodException; MainViewModelTest guards this.
 *
 * All disk and document I/O happens in the injected dependencies on background dispatchers;
 * state is only ever touched on the main thread.
 */
class MainViewModel(
    application: Application,
    repository: FoodDensitySource,
    private val historyStore: HistoryStore,
    private val documents: DocumentAccess
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        FoodDensityRepository(application),
        HistoryManager(application),
        ContentResolverDocuments(application)
    )

    private var allFoodItems: List<FoodDensity> = emptyList()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            allFoodItems = repository.loadFoodDensities()
            val history = historyStore.load()
            // Published together, so history (and thus history clicks) only appears once foods
            // are available. Re-run any search typed while loading.
            _uiState.update {
                it.copy(
                    history = history,
                    foodSearchResults = if (it.selectedFood == null) searchFoods(it.foodSearchQuery) else emptyList()
                )
            }
        }
    }

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

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

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
        val history = HistoryRules.record(
            state.history,
            HistoryEntry(
                unitSelection = selection.name,
                unitValue = state.price,
                densitySelection = food.foodName,
                comment = state.comment
            )
        )

        _uiState.update { it.copy(result = result, priceError = null, history = history) }
        persist(history)
    }

    /** Clears the inputs and results; the chosen currency and history are kept. */
    fun resetInput() = _uiState.update { MainUiState(currency = it.currency, history = it.history) }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
        viewModelScope.launch {
            if (attempt { historyStore.clear() } == null) showMessage("Couldn't clear saved history")
        }
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

    fun exportHistory(uri: String) {
        val bytes = HistoryCodec.encode(_uiState.value.history).toByteArray(Charsets.UTF_8)
        viewModelScope.launch {
            val written = attempt { documents.write(uri, bytes) }
            showMessage(if (written != null) "History exported" else "Couldn't export history")
        }
    }

    fun importHistory(uri: String) {
        viewModelScope.launch {
            val imported = attempt { HistoryCodec.decode(String(documents.read(uri), Charsets.UTF_8)) }
            if (imported == null) {
                showMessage("Couldn't read that file as history")
                return@launch
            }
            val before = _uiState.value.history
            val merged = HistoryRules.merge(before, imported)
            _uiState.update { it.copy(history = merged) }
            persist(merged)
            val added = merged.size - before.size
            showMessage(if (added == 1) "Imported 1 new entry" else "Imported $added new entries")
        }
    }

    private fun persist(history: List<HistoryEntry>) {
        viewModelScope.launch {
            if (attempt { historyStore.save(history) } == null) showMessage("Couldn't save history")
        }
    }

    private fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    /** Runs [block], turning any failure (but not cancellation) into null. */
    private suspend fun <T> attempt(block: suspend () -> T): T? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
}
