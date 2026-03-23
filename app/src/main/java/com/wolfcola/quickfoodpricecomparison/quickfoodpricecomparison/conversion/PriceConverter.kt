package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.UnitConversionFactors
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import kotlin.math.roundToLong

object PriceConverter {

    fun convertPriceForUnit(
        price: Double,
        sourceMassInGrams: Double,
        target: ConversionUnit,
        densityGPerMl: Double
    ): Double {
        val result = if (target.isMass) {
            val gramsPerTargetUnit = UnitConversionFactors.gramsPerUnit[target.unit]
                ?: throw IllegalArgumentException("Unknown mass unit: ${target.unit}")
            price / (sourceMassInGrams / gramsPerTargetUnit) * target.value
        } else {
            val mlPerTargetUnit = UnitConversionFactors.mlPerUnit[target.unit]
                ?: throw IllegalArgumentException("Unknown volume unit: ${target.unit}")
            val sourceVolumeInTargetUnits = (sourceMassInGrams / densityGPerMl) / mlPerTargetUnit
            price / sourceVolumeInTargetUnits * target.value
        }
        return (result * 100000.0).roundToLong() / 100000.0
    }

    fun convertAllPrices(
        price: Double,
        sourceMassInGrams: Double,
        densityGPerMl: Double,
        units: List<ConversionUnit>
    ): Map<String, Double> {
        require(price >= 0) { "Price must be non-negative" }
        require(densityGPerMl > 0) { "Density must be positive" }

        return units.associate { unit ->
            unit.key to convertPriceForUnit(price, sourceMassInGrams, unit, densityGPerMl)
        }
    }
}
