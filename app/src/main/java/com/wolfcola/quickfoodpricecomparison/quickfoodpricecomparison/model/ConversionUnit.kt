package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

/**
 * One "target unit" a price can be converted into, e.g. "price per 500 grams".
 *
 * As a data class, (symbol, value) equality/hashCode makes ConversionUnit itself a safe,
 * unique map key — no separate derived string key is needed to identify one.
 */
data class ConversionUnit(
    val symbol: UnitSymbol,
    val value: Double
) {
    val category: UnitCategory get() = symbol.category
    val isMass: Boolean get() = symbol.isMass
    val isVolume: Boolean get() = symbol.isVolume

    fun displayLabel(): String {
        val valueStr = if (value == 0.5) "0.5" else value.toInt().toString()
        return "Price per $valueStr ${symbol.displayName}:"
    }
}
