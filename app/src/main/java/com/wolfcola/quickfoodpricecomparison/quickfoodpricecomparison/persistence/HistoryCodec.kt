package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import org.json.JSONArray

/** The JSON format shared by the on-disk history file and exported history files. */
object HistoryCodec {

    fun encode(entries: List<HistoryEntry>): String =
        JSONArray().apply { entries.forEach { put(it.toJson()) } }.toString()

    /** @throws org.json.JSONException if [json] is not a JSON array of objects. */
    fun decode(json: String): List<HistoryEntry> {
        val array = JSONArray(json)
        return (0 until array.length()).map { HistoryEntry.fromJson(array.getJSONObject(it)) }
    }
}
