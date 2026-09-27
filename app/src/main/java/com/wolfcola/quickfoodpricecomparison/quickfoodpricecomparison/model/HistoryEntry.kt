package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

import org.json.JSONObject

data class HistoryEntry(
    val unitSelection: String? = null,
    val unitValue: String? = null,
    val densitySelection: String? = null,
    val comment: String? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("unit_selection", unitSelection ?: JSONObject.NULL)
        put("unit_value", unitValue ?: JSONObject.NULL)
        put("density_selection", densitySelection ?: JSONObject.NULL)
        put("comment", comment ?: JSONObject.NULL)
    }

    fun isEmpty(): Boolean =
        densitySelection == null && unitSelection == null && unitValue == null

    companion object {
        fun fromJson(obj: JSONObject): HistoryEntry = HistoryEntry(
            unitSelection = obj.optStringOrNull("unit_selection"),
            unitValue = obj.optStringOrNull("unit_value"),
            densitySelection = obj.optStringOrNull("density_selection"),
            comment = obj.optStringOrNull("comment"),
        )
    }
}

/** Missing keys and explicit JSON nulls both map to Kotlin null (optString alone returns ""/"null"). */
private fun JSONObject.optStringOrNull(key: String): String? =
    if (isNull(key)) null else optString(key)
