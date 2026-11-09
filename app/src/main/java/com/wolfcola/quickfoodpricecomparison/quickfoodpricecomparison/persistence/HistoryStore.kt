package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.persistence

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.HistoryEntry

/**
 * Seam between MainViewModel and where history is actually persisted, so tests can supply an
 * in-memory fake instead of a real HistoryManager (which needs a Context and touches disk).
 *
 * Implementations only read and write; what belongs in the list is decided by HistoryRules.
 */
interface HistoryStore {
    suspend fun load(): List<HistoryEntry>
    suspend fun save(entries: List<HistoryEntry>)
    suspend fun clear()
}
