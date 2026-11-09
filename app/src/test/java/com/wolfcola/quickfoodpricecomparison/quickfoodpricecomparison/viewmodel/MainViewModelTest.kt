package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensitySource
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FakeFoodDensitySource(private val items: List<FoodDensity>) : FoodDensitySource {
    override fun loadFoodDensities(): List<FoodDensity> = items
}

private class FakeHistoryStore(initial: List<HistoryEntry> = emptyList()) : HistoryStore {
    var saved: List<HistoryEntry> = initial
        private set
    var cleared = false
        private set

    override fun loadHistory(): List<HistoryEntry> = saved
    override fun saveHistory(entries: List<HistoryEntry>) {
        saved = entries
    }

    override fun clearHistory() {
        cleared = true
        saved = emptyList()
    }

    override fun historyToBytes(entries: List<HistoryEntry>): ByteArray =
        entries.joinToString(";") { "${it.densitySelection}:${it.unitSelection}:${it.unitValue}" }
            .toByteArray(Charsets.UTF_8)
}

/**
 * These fakes are only possible because MainViewModel takes FoodDensitySource/HistoryStore as
 * constructor parameters instead of constructing the real, Context-backed classes itself.
 */
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val water = FoodDensity(foodName = "Water", gMl = 1.0, category = "Beverages")
    private val milk = FoodDensity(foodName = "Milk", gMl = 1.03, category = "Dairy")

    private fun newViewModel(
        items: List<FoodDensity> = listOf(water, milk),
        history: FakeHistoryStore = FakeHistoryStore()
    ) = MainViewModel(
        application = Application(),
        repository = FakeFoodDensitySource(items),
        historyManager = history
    )

    @Test
    fun `exposes the single-argument constructor AndroidViewModelFactory looks up`() {
        // Exactly the lookup ViewModelProvider.AndroidViewModelFactory performs before
        // instantiating the ViewModel. Kotlin default arguments do not emit this constructor,
        // so without an explicit one the app crashes at startup with NoSuchMethodException.
        assertNotNull(MainViewModel::class.java.getConstructor(Application::class.java))
    }

    @Test
    fun `performConversion populates results and price per unit`() {
        val viewModel = newViewModel()
        viewModel.onFoodItemDirectlySelected(water)
        viewModel.onPriceChanged("10")

        viewModel.performConversion()

        val state = viewModel.uiState.value
        assertEquals(0.01, state.result!!.pricePerGram, 0.0)
        assertTrue(state.result!!.prices.isNotEmpty())
        assertTrue(state.showClearButton)
    }

    @Test
    fun `performConversion without a selected food does nothing`() {
        val viewModel = newViewModel()
        viewModel.onPriceChanged("10")

        viewModel.performConversion()

        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun `performConversion saves an entry to history`() {
        val history = FakeHistoryStore()
        val viewModel = newViewModel(history = history)
        viewModel.onFoodItemDirectlySelected(water)
        viewModel.onPriceChanged("10")

        viewModel.performConversion()

        assertEquals(1, history.saved.size)
        assertEquals("Water", history.saved[0].densitySelection)
        assertEquals(history.saved, viewModel.uiState.value.history)
    }

    @Test
    fun `invalid price sets an error instead of converting or saving history`() {
        for (bad in listOf("-5", "", "abc", "NaN", "Infinity")) {
            val history = FakeHistoryStore()
            val viewModel = newViewModel(history = history)
            viewModel.onFoodItemDirectlySelected(water)
            viewModel.onPriceChanged(bad)

            viewModel.performConversion()

            val state = viewModel.uiState.value
            assertNotNull("error for '$bad'", state.priceError)
            assertNull("result for '$bad'", state.result)
            assertTrue("history for '$bad'", history.saved.isEmpty())
        }
    }

    @Test
    fun `editing the price clears the error`() {
        val viewModel = newViewModel()
        viewModel.onFoodItemDirectlySelected(water)
        viewModel.onPriceChanged("-5")
        viewModel.performConversion()

        viewModel.onPriceChanged("5")

        assertNull(viewModel.uiState.value.priceError)
    }

    @Test
    fun `selecting a history entry with a negative price does not crash`() {
        val bad = HistoryEntry("Price per kilogram", "-5", "Water", "")
        val viewModel = newViewModel(history = FakeHistoryStore(initial = listOf(bad)))

        viewModel.onHistoryItemSelected(bad)

        assertNotNull(viewModel.uiState.value.priceError)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun `resetInput clears conversion state but keeps currency and history`() {
        val viewModel = newViewModel()
        viewModel.onCurrencyChanged("€")
        viewModel.onFoodItemDirectlySelected(water)
        viewModel.onPriceChanged("10")
        viewModel.performConversion()

        viewModel.resetInput()

        val state = viewModel.uiState.value
        assertEquals("1", state.price)
        assertEquals("€", state.currency)
        assertNull(state.selectedFood)
        assertNull(state.result)
        assertFalse(state.showClearButton)
        assertEquals(1, state.history.size)
    }

    @Test
    fun `clearHistory delegates to the history store`() {
        val history = FakeHistoryStore(initial = listOf(HistoryEntry("kg", "10", "Water", "")))
        val viewModel = newViewModel(history = history)

        viewModel.clearHistory()

        assertTrue(history.cleared)
        assertTrue(viewModel.uiState.value.history.isEmpty())
    }

    @Test
    fun `food search filters by name and category, case-insensitively`() {
        val viewModel = newViewModel()

        viewModel.onFoodSearchQueryChanged("dairy")

        assertEquals(listOf(milk), viewModel.uiState.value.foodSearchResults)
    }

    @Test
    fun `selecting a food directly clears the search results`() {
        val viewModel = newViewModel()
        viewModel.onFoodSearchQueryChanged("wat")

        viewModel.onFoodItemDirectlySelected(water)

        val state = viewModel.uiState.value
        assertEquals(water, state.selectedFood)
        assertEquals("Water", state.foodSearchQuery)
        assertTrue(state.foodSearchResults.isEmpty())
    }
}
