package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import kotlin.math.roundToLong

object PriceConverter {

    /** Rounds to 5 decimal places, the precision used throughout price conversions. */
    fun round5(value: Double): Double = (value * 100000.0).roundToLong() / 100000.0

    fun convertPriceForUnit(
        price: Double,
        sourceMassInGrams: Double,
        target: ConversionUnit,
        densityGPerMl: Double
    ): Double {
        val result = if (target.isMass) {
            price / (sourceMassInGrams / target.symbol.baseAmount) * target.value
        } else {
            val sourceVolumeInTargetUnits = (sourceMassInGrams / densityGPerMl) / target.symbol.baseAmount
            price / sourceVolumeInTargetUnits * target.value
        }
        return round5(result)
    }

    fun convertAllPrices(
        price: Double,
        sourceMassInGrams: Double,
        densityGPerMl: Double,
        units: List<ConversionUnit>
    ): Map<ConversionUnit, Double> {
        require(price >= 0) { "Price must be non-negative" }
        require(densityGPerMl > 0) { "Density must be positive" }

        return units.associateWith { unit ->
            convertPriceForUnit(price, sourceMassInGrams, unit, densityGPerMl)
        }
    }
}
