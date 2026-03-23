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
            unitSelection = if (obj.isNull("unit_selection")) null else obj.optString("unit_selection"),
            unitValue = if (obj.isNull("unit_value")) null else obj.optString("unit_value"),
            densitySelection = if (obj.isNull("density_selection")) null else obj.optString("density_selection"),
            comment = if (obj.isNull("comment")) null else obj.optString("comment"),
        )
    }
}
