package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class FoodDensityRepositoryTest {

    @Test
    fun `parseCsvLine handles simple fields`() {
        val result = FoodDensityRepository.parseCsvLine("a,b,c")
        assertEquals(listOf("a", "b", "c"), result)
    }

    @Test
    fun `parseCsvLine handles quoted fields with commas`() {
        val result = FoodDensityRepository.parseCsvLine("\"alcohol, ethyl\",0.789,0.789,USDA,\"Beverages, alcoholic\"")
        assertEquals("alcohol, ethyl", result[0])
        assertEquals("0.789", result[1])
        assertEquals("Beverages, alcoholic", result[4])
    }

    @Test
    fun `parseCsv parses valid CSV`() {
        val csv = "food_name,g_ml,specific_gravity,biblio_id,category\n" +
                "water,1.0,1.0,USDA,Beverages\n" +
                "\"alcohol, ethyl\",0.789,0.789,USDA,\"Beverages, alcoholic\"\n"
        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val items = FoodDensityRepository.parseCsv(stream)
        assertEquals(2, items.size)
        assertEquals("water", items[0].foodName)
        assertEquals(1.0, items[0].gMl, 0.001)
        assertEquals("alcohol, ethyl", items[1].foodName)
        assertEquals(0.789, items[1].gMl, 0.001)
        assertEquals("Beverages, alcoholic", items[1].category)
    }

    @Test
    fun `parseCsv handles BOM`() {
        val csv = "\uFEFFfood_name,g_ml\nwater,1.0\n"
        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val items = FoodDensityRepository.parseCsv(stream)
        assertEquals(1, items.size)
        assertEquals("water", items[0].foodName)
    }

    @Test
    fun `parseCsv skips malformed rows`() {
        val csv = "food_name,g_ml\nwater,1.0\nbad,notanumber\ngood,2.0\n"
        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val items = FoodDensityRepository.parseCsv(stream)
        assertEquals(2, items.size)
    }

    @Test
    fun `getFoodCategories returns sorted unique categories`() {
        val items = listOf(
            FoodDensity("a", 1.0, category = "Dairy"),
            FoodDensity("b", 1.0, category = "Beverages"),
            FoodDensity("c", 1.0, category = "Dairy"),
            FoodDensity("d", 1.0, category = ""),
        )
        val cats = FoodDensityRepository.getFoodCategories(items)
        assertEquals(listOf("Beverages", "Dairy"), cats)
    }

    @Test
    fun `parseCsv handles range in specific_gravity`() {
        val csv = "food_name,g_ml,specific_gravity\nflour,0.6,0.59-0.62\n"
        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val items = FoodDensityRepository.parseCsv(stream)
        assertEquals(1, items.size)
        assertEquals(null, items[0].specificGravity) // range can't parse to Double
    }
}
