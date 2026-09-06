package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

/**
 * Single source of truth for a unit's display name, category, and its size relative to the
 * base unit for its category (grams for mass, milliliters for volume).
 *
 * Previously this data was scattered across three places that had to be kept in sync by hand:
 * UnitConversionFactors' gramsPerUnit/mlPerUnit maps, a separate display-name map inside
 * ConversionUnit.displayLabel(), and a per-entry UnitCategory argument on every CONVERSION_UNITS
 * row. Consolidating it here means adding a unit only means adding one enum constant.
 */
enum class UnitSymbol(
    val displayName: String,
    val category: UnitCategory,
    val baseAmount: Double
) {
    KG("kilogram", UnitCategory.METRIC_MASS, 1000.0),
    G("gram", UnitCategory.METRIC_MASS, 1.0),
    MG("milligram", UnitCategory.METRIC_MASS, 0.001),
    LBS("pound", UnitCategory.IMPERIAL_MASS, 453.59237),
    OZ("ounce", UnitCategory.IMPERIAL_MASS, 28.349523125),
    L("liter", UnitCategory.METRIC_VOLUME, 1000.0),
    ML("milliliter", UnitCategory.METRIC_VOLUME, 1.0),
    GALLON("gallon", UnitCategory.IMPERIAL_VOLUME, 3785.411784),
    QUART("quart", UnitCategory.IMPERIAL_VOLUME, 946.352946),
    PINT("pint", UnitCategory.IMPERIAL_VOLUME, 473.176473),
    FLUID_OUNCE("fluid ounce", UnitCategory.IMPERIAL_VOLUME, 29.5735295625),
    CUP("cup", UnitCategory.IMPERIAL_VOLUME, 236.5882365);

    val isMass: Boolean get() = category.isMass
    val isVolume: Boolean get() = category.isVolume
}
