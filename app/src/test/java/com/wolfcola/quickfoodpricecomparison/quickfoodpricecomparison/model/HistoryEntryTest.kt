package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryEntryTest {

    @Test
    fun `isEmpty returns true when all key fields null`() {
        val entry = HistoryEntry()
        assertTrue(entry.isEmpty())
    }

    @Test
    fun `isEmpty returns false when unitSelection set`() {
        val entry = HistoryEntry(unitSelection = "Price per kilogram")
        assertFalse(entry.isEmpty())
    }

    @Test
    fun `isEmpty returns false when densitySelection set`() {
        val entry = HistoryEntry(densitySelection = "water")
        assertFalse(entry.isEmpty())
    }

    @Test
    fun `toJson and fromJson round-trip`() {
        val original = HistoryEntry(
            unitSelection = "Price per kilogram",
            unitValue = "5.0",
            densitySelection = "water",
            comment = "test"
        )
        val json = original.toJson()
        val restored = HistoryEntry.fromJson(json)
        assertEquals(original.unitSelection, restored.unitSelection)
        assertEquals(original.unitValue, restored.unitValue)
        assertEquals(original.densitySelection, restored.densitySelection)
        assertEquals(original.comment, restored.comment)
    }
}
