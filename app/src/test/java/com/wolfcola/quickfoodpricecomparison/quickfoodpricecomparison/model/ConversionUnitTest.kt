package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversionUnitTest {

    @Test
    fun `key for kg 1 is kg_1`() {
        val unit = ConversionUnit("kg", 1.0, UnitCategory.METRIC_MASS)
        assertEquals("kg_1", unit.key)
    }

    @Test
    fun `key for g 500 is g_500`() {
        val unit = ConversionUnit("g", 500.0, UnitCategory.METRIC_MASS)
        assertEquals("g_500", unit.key)
    }

    @Test
    fun `key for gallon half is gallon_half`() {
        val unit = ConversionUnit("gallon", 0.5, UnitCategory.IMPERIAL_VOLUME)
        assertEquals("gallon_half", unit.key)
    }

    @Test
    fun `key for fluid_ounce is fluidounce_1`() {
        val unit = ConversionUnit("fluid_ounce", 1.0, UnitCategory.IMPERIAL_VOLUME)
        assertEquals("fluidounce_1", unit.key)
    }

    @Test
    fun `isMass true for metric mass`() {
        val unit = ConversionUnit("kg", 1.0, UnitCategory.METRIC_MASS)
        assertTrue(unit.isMass)
        assertFalse(unit.isVolume)
    }

    @Test
    fun `isMass true for imperial mass`() {
        val unit = ConversionUnit("lbs", 1.0, UnitCategory.IMPERIAL_MASS)
        assertTrue(unit.isMass)
        assertFalse(unit.isVolume)
    }

    @Test
    fun `isVolume true for metric volume`() {
        val unit = ConversionUnit("l", 1.0, UnitCategory.METRIC_VOLUME)
        assertFalse(unit.isMass)
        assertTrue(unit.isVolume)
    }

    @Test
    fun `isVolume true for imperial volume`() {
        val unit = ConversionUnit("gallon", 1.0, UnitCategory.IMPERIAL_VOLUME)
        assertFalse(unit.isMass)
        assertTrue(unit.isVolume)
    }
}
