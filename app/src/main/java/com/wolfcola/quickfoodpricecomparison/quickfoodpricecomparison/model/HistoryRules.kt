package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

/**
 * The one place that decides what a valid history list looks like. Every path that changes
 * history (converting, importing, loading from disk) goes through here, so the in-memory list
 * and the file on disk always agree.
 */
object HistoryRules {

    /** Drops entries with no unit, value or food, and exact duplicates (keeping the first). */
    fun normalize(entries: List<HistoryEntry>): List<HistoryEntry> =
        entries.filterNot { it.isEmpty() }.distinct()

    /**
     * Appends [entry] as the newest item. An older entry for the same conversion (same food, unit
     * and price) is replaced even if its comment differs, so re-running a conversion moves it to
     * the end instead of duplicating it.
     */
    fun record(history: List<HistoryEntry>, entry: HistoryEntry): List<HistoryEntry> =
        normalize(history).filterNot { it.isSameConversionAs(entry) } + entry

    /** Appends [imported] after [history], skipping anything already present. */
    fun merge(history: List<HistoryEntry>, imported: List<HistoryEntry>): List<HistoryEntry> =
        normalize(history + imported)

    private fun HistoryEntry.isSameConversionAs(other: HistoryEntry): Boolean =
        densitySelection == other.densitySelection &&
            unitSelection == other.unitSelection &&
            unitValue == other.unitValue
}
