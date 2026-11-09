package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryCodecTest {

    @Test
    fun `encode and decode round-trip, including nulls`() {
        val entries = listOf(
            HistoryEntry("kg", "10", "water", "test"),
            HistoryEntry("lbs", "5", "milk", null),
        )
        assertEquals(entries, HistoryCodec.decode(HistoryCodec.encode(entries)))
    }

    @Test
    fun `decodes the existing on-disk format`() {
        val json = """[{"unit_selection":"Price per kilogram","unit_value":"2","density_selection":"water","comment":null}]"""
        assertEquals(
            listOf(HistoryEntry("Price per kilogram", "2", "water", null)),
            HistoryCodec.decode(json)
        )
    }

    @Test(expected = JSONException::class)
    fun `decode rejects non-json`() {
        HistoryCodec.decode("not json")
    }

    @Test(expected = JSONException::class)
    fun `decode rejects an array of non-objects`() {
        HistoryCodec.decode("[1, 2]")
    }
}
