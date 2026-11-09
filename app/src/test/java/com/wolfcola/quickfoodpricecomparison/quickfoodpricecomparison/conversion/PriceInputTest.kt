package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceInputTest {

    @Test
    fun `accepts non-negative numbers, ignoring surrounding whitespace`() {
        assertEquals(PriceInput.Valid(0.0), PriceInput.parse("0"))
        assertEquals(PriceInput.Valid(12.5), PriceInput.parse(" 12.5 "))
    }

    @Test
    fun `rejects blank, non-numeric, non-finite and negative input`() {
        for (bad in listOf("", "  ", "abc", "1,5", "NaN", "Infinity", "-0.01")) {
            assertTrue("'$bad' should be invalid", PriceInput.parse(bad) is PriceInput.Invalid)
        }
    }

    @Test
    fun `formatPrice uses plain notation for small values`() {
        assertEquals("$0.00035", formatPrice("$", 0.00035))
        assertEquals("$0.01", formatPrice("$", 0.01))
    }

    @Test
    fun `formatPrice drops trailing zeros`() {
        assertEquals("€10", formatPrice("€", 10.0))
        assertEquals("€2.5", formatPrice("€", 2.50))
        assertEquals("€0", formatPrice("€", 0.0))
    }
}
