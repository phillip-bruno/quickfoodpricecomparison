package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import android.content.Context
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry
import org.json.JSONArray
import java.io.File

class HistoryManager(private val context: Context) {

    private val historyFile: File
        get() = File(context.filesDir, "conversion_history.json")

    fun loadHistory(): List<HistoryEntry> {
        val file = historyFile
        if (!file.exists()) return emptyList()
        return try {
            parseJsonEntries(file.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveHistory(entries: List<HistoryEntry>) {
        val deduped = deduplicateEntries(entries)
        val array = JSONArray()
        deduped.forEach { array.put(it.toJson()) }
        historyFile.writeText(array.toString())
    }

    fun clearHistory() {
        val file = historyFile
        if (file.exists()) file.delete()
    }

    fun historyToBytes(entries: List<HistoryEntry>): ByteArray {
        val deduped = deduplicateEntries(entries)
        val array = JSONArray()
        deduped.forEach { array.put(it.toJson()) }
        return array.toString().toByteArray(Charsets.UTF_8)
    }

    companion object {
        fun deduplicateEntries(entries: List<HistoryEntry>): List<HistoryEntry> {
            val seen = mutableSetOf<String>()
            return entries.filter { entry ->
                val key = "${entry.unitSelection}|${entry.unitValue}|${entry.densitySelection}|${entry.comment}"
                seen.add(key)
            }
        }

        fun cleanupHistory(
            entries: List<HistoryEntry>,
            currentDensity: String?,
            currentUnit: String?,
            currentValue: String?
        ): List<HistoryEntry> {
            return entries.filter { entry ->
                !entry.isEmpty() && !(
                    entry.densitySelection == currentDensity &&
                    entry.unitSelection == currentUnit &&
                    entry.unitValue == currentValue
                )
            }
        }

        fun parseJsonEntries(json: String): List<HistoryEntry> {
            val array = JSONArray(json)
            return (0 until array.length()).map { i ->
                HistoryEntry.fromJson(array.getJSONObject(i))
            }
        }
    }
}
