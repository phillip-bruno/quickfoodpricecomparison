package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel

import android.app.Application
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.FoodDensitySource
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence.HistoryStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
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

        assertEquals("$0.01", viewModel.pricePerUnit.value)
        assertTrue(viewModel.results.value.isNotEmpty())
        assertTrue(viewModel.showClearButton.value)
    }

    @Test
    fun `performConversion without a selected food does nothing`() {
        val viewModel = newViewModel()
        viewModel.onPriceChanged("10")

        viewModel.performConversion()

        assertTrue(viewModel.results.value.isEmpty())
        assertEquals("", viewModel.pricePerUnit.value)
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
    }

    @Test
    fun `resetInput clears conversion state`() {
        val viewModel = newViewModel()
        viewModel.onFoodItemDirectlySelected(water)
        viewModel.onPriceChanged("10")
        viewModel.performConversion()

        viewModel.resetInput()

        assertEquals("1", viewModel.price.value)
        assertNull(viewModel.getSelectedFood())
        assertTrue(viewModel.results.value.isEmpty())
        assertEquals(false, viewModel.showClearButton.value)
    }

    @Test
    fun `clearHistory delegates to the history store`() {
        val history = FakeHistoryStore(initial = listOf(HistoryEntry("kg", "10", "Water", "")))
        val viewModel = newViewModel(history = history)

        viewModel.clearHistory()

        assertTrue(history.cleared)
        assertTrue(viewModel.historyEntries.value.isEmpty())
    }

    @Test
    fun `food search filters by name and category, case-insensitively`() {
        val viewModel = newViewModel()

        viewModel.onFoodSearchQueryChanged("dairy")

        assertEquals(listOf(milk), viewModel.foodSearchResults.value)
    }

    @Test
    fun `selecting a food directly clears the search results`() {
        val viewModel = newViewModel()
        viewModel.onFoodSearchQueryChanged("wat")

        viewModel.onFoodItemDirectlySelected(water)

        assertEquals(water, viewModel.getSelectedFood())
        assertEquals("Water", viewModel.foodSearchQuery.value)
    }
}
