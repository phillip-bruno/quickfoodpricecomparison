package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.UnitCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceConverterTest {

    @Test
    fun `convert mass same unit kg to kg`() {
        // price=10, source=1kg (1000g), target=1kg -> 10.0
        val results = PriceConverter.convertAllPrices(
            price = 10.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 1.0,
            units = CONVERSION_UNITS
        )
        assertEquals(10.0, results["kg_1"]!!, 0.00001)
    }

    @Test
    fun `convert mass kg to 100g`() {
        // price=10, source=1kg (1000g), target=100g
        // 10 / (1000/1) * 100 = 1.0
        val results = PriceConverter.convertAllPrices(
            price = 10.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 1.0,
            units = CONVERSION_UNITS
        )
        assertEquals(1.0, results["g_100"]!!, 0.00001)
    }

    @Test
    fun `convert volume with density 1`() {
        // price=10, source=1kg (1000g), density=1.0 g/ml
        // volume = 1000g / 1.0 = 1000 ml = 1 liter
        // target=1L: 10 / (1000/1.0/1000) * 1 = 10.0
        val results = PriceConverter.convertAllPrices(
            price = 10.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 1.0,
            units = CONVERSION_UNITS
        )
        assertEquals(10.0, results["l_1"]!!, 0.00001)
    }

    @Test
    fun `convert volume with density 0_789`() {
        // price=10, source=1kg (1000g), density=0.789 g/ml
        // target=100ml: 10 / (1000/0.789/1) * 100 = 10 / 1267.427... * 100 = 0.789
        val results = PriceConverter.convertAllPrices(
            price = 10.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 0.789,
            units = CONVERSION_UNITS
        )
        assertEquals(0.789, results["ml_100"]!!, 0.001)
    }

    @Test
    fun `convert all prices with zero price returns all zeros`() {
        val results = PriceConverter.convertAllPrices(
            price = 0.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 1.0,
            units = CONVERSION_UNITS
        )
        results.values.forEach { assertEquals(0.0, it, 0.00001) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative price throws`() {
        PriceConverter.convertAllPrices(
            price = -1.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 1.0,
            units = CONVERSION_UNITS
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero density throws`() {
        PriceConverter.convertAllPrices(
            price = 10.0,
            sourceMassInGrams = 1000.0,
            densityGPerMl = 0.0,
            units = CONVERSION_UNITS
        )
    }

    @Test
    fun `single mass unit conversion`() {
        val unit = ConversionUnit("oz", 1.0, UnitCategory.IMPERIAL_MASS)
        // price=10, source=1kg (1000g), target=1oz (28.3495g)
        // 10 / (1000/28.349523125) * 1 = 10 / 35.2739... = 0.28350 (rounded to 5 dp)
        val result = PriceConverter.convertPriceForUnit(10.0, 1000.0, unit, 1.0)
        assertEquals(0.28350, result, 0.001)
    }
}
