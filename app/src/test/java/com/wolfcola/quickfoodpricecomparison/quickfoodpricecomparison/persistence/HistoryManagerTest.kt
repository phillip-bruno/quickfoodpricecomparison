package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryManagerTest {

    @Test
    fun `deduplicateEntries removes duplicates`() {
        val entries = listOf(
            HistoryEntry("kg", "10", "water", "a"),
            HistoryEntry("kg", "10", "water", "a"),
            HistoryEntry("lbs", "5", "milk", "b"),
        )
        val result = HistoryManager.deduplicateEntries(entries)
        assertEquals(2, result.size)
        assertEquals("kg", result[0].unitSelection)
        assertEquals("lbs", result[1].unitSelection)
    }

    @Test
    fun `cleanupHistory removes empty entries`() {
        val entries = listOf(
            HistoryEntry(), // empty
            HistoryEntry("kg", "10", "water", "a"),
        )
        val result = HistoryManager.cleanupHistory(entries, "milk", "lbs", "5")
        assertEquals(1, result.size)
        assertEquals("water", result[0].densitySelection)
    }

    @Test
    fun `cleanupHistory removes current entry duplicates`() {
        val entries = listOf(
            HistoryEntry("kg", "10", "water", "a"),
            HistoryEntry("lbs", "5", "milk", "b"),
        )
        val result = HistoryManager.cleanupHistory(entries, "water", "kg", "10")
        assertEquals(1, result.size)
        assertEquals("milk", result[0].densitySelection)
    }

    @Test
    fun `parseJsonEntries round-trip`() {
        val entries = listOf(
            HistoryEntry("kg", "10", "water", "test"),
            HistoryEntry("lbs", "5", "milk", ""),
        )
        val array = JSONArray()
        entries.forEach { array.put(it.toJson()) }
        val json = array.toString()

        val parsed = HistoryManager.parseJsonEntries(json)
        assertEquals(2, parsed.size)
        assertEquals("water", parsed[0].densitySelection)
        assertEquals("milk", parsed[1].densitySelection)
    }

    @Test
    fun `parseJsonEntries with invalid json returns exception`() {
        try {
            HistoryManager.parseJsonEntries("not json")
            assertTrue("Should have thrown", false)
        } catch (_: Exception) {
            // expected
        }
    }
}
