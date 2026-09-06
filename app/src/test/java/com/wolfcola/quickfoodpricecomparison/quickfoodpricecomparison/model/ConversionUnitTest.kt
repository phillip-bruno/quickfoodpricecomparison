package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversionUnitTest {

    @Test
    fun `equal symbol and value are treated as the same unit`() {
        assertEquals(ConversionUnit(UnitSymbol.KG, 1.0), ConversionUnit(UnitSymbol.KG, 1.0))
        assertFalse(ConversionUnit(UnitSymbol.KG, 1.0) == ConversionUnit(UnitSymbol.G, 1.0))
        assertFalse(ConversionUnit(UnitSymbol.G, 500.0) == ConversionUnit(UnitSymbol.G, 100.0))
    }

    @Test
    fun `displayLabel for g 500 is Price per 500 gram`() {
        val unit = ConversionUnit(UnitSymbol.G, 500.0)
        assertEquals("Price per 500 gram:", unit.displayLabel())
    }

    @Test
    fun `displayLabel for gallon half is Price per 0_5 gallon`() {
        val unit = ConversionUnit(UnitSymbol.GALLON, 0.5)
        assertEquals("Price per 0.5 gallon:", unit.displayLabel())
    }

    @Test
    fun `displayLabel for fluid_ounce uses its display name`() {
        val unit = ConversionUnit(UnitSymbol.FLUID_OUNCE, 1.0)
        assertEquals("Price per 1 fluid ounce:", unit.displayLabel())
    }

    @Test
    fun `isMass true for metric mass`() {
        val unit = ConversionUnit(UnitSymbol.KG, 1.0)
        assertTrue(unit.isMass)
        assertFalse(unit.isVolume)
    }

    @Test
    fun `isMass true for imperial mass`() {
        val unit = ConversionUnit(UnitSymbol.LBS, 1.0)
        assertTrue(unit.isMass)
        assertFalse(unit.isVolume)
    }

    @Test
    fun `isVolume true for metric volume`() {
        val unit = ConversionUnit(UnitSymbol.L, 1.0)
        assertFalse(unit.isMass)
        assertTrue(unit.isVolume)
    }

    @Test
    fun `isVolume true for imperial volume`() {
        val unit = ConversionUnit(UnitSymbol.GALLON, 1.0)
        assertFalse(unit.isMass)
        assertTrue(unit.isVolume)
    }
}
