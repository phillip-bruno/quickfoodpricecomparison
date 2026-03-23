package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

object UnitConversionFactors {
    val gramsPerUnit: Map<String, Double> = mapOf(
        "kg" to 1000.0,
        "g" to 1.0,
        "mg" to 0.001,
        "lbs" to 453.59237,
        "oz" to 28.349523125
    )

    val mlPerUnit: Map<String, Double> = mapOf(
        "l" to 1000.0,
        "ml" to 1.0,
        "gallon" to 3785.411784,
        "quart" to 946.352946,
        "pint" to 473.176473,
        "fluid_ounce" to 29.5735295625,
        "cup" to 236.5882365
    )
}
