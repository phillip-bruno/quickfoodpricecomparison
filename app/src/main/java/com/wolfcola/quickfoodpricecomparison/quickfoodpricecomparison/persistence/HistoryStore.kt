package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry

/**
 * Seam between MainViewModel and where history is actually persisted, so tests can supply an
 * in-memory fake instead of a real HistoryManager (which needs a Context and touches disk).
 */
interface HistoryStore {
    fun loadHistory(): List<HistoryEntry>
    fun saveHistory(entries: List<HistoryEntry>)
    fun clearHistory()
    fun historyToBytes(entries: List<HistoryEntry>): ByteArray
}
