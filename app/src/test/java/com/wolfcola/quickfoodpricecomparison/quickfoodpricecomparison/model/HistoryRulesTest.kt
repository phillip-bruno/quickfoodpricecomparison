package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryRulesTest {

    private val water = HistoryEntry("kg", "10", "water", "a")
    private val milk = HistoryEntry("lbs", "5", "milk", "b")

    @Test
    fun `normalize drops empty entries and exact duplicates, keeping order`() {
        assertEquals(listOf(water, milk), HistoryRules.normalize(listOf(water, HistoryEntry(), milk, water)))
    }

    @Test
    fun `record appends the entry as newest`() {
        val entry = HistoryEntry("g", "3", "oil", "")
        assertEquals(listOf(water, milk, entry), HistoryRules.record(listOf(water, milk), entry))
    }

    @Test
    fun `record replaces an older run of the same conversion even with a different comment`() {
        val rerun = water.copy(comment = "new comment")
        assertEquals(listOf(milk, rerun), HistoryRules.record(listOf(water, milk), rerun))
    }

    @Test
    fun `merge appends only entries not already present`() {
        val oil = HistoryEntry("g", "3", "oil", "")
        assertEquals(
            listOf(water, milk, oil),
            HistoryRules.merge(listOf(water, milk), listOf(milk, oil, oil, HistoryEntry()))
        )
    }
}
