package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

data class ConversionUnit(
    val unit: String,
    val value: Double,
    val category: UnitCategory
) {
    val key: String
        get() {
            val valStr = if (value == 0.5) "half" else value.toInt().toString()
            val unitStr = if (unit == "fluid_ounce") "fluidounce" else unit
            return "${unitStr}_$valStr"
        }

    val isMass: Boolean get() = category.isMass
    val isVolume: Boolean get() = category.isVolume

    fun displayLabel(): String {
        val unitNames = mapOf(
            "kg" to "kilogram", "g" to "gram", "mg" to "milligram",
            "lbs" to "pound", "oz" to "ounce", "l" to "liter",
            "ml" to "milliliter", "gallon" to "gallon", "quart" to "quart",
            "pint" to "pint", "fluid_ounce" to "fluid_ounce", "cup" to "cup"
        )
        val name = unitNames[unit] ?: unit
        val valueStr = if (value == 0.5) "0.5" else value.toInt().toString()
        return "Price per $valueStr $name:"
    }
}
