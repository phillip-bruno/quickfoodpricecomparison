package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.SelectionItem
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.UnitCategory

const val ALL_CATEGORIES_LABEL = "All Categories"

val SELECTION_LIST = listOf(
    SelectionItem("Price per kilogram", 1000.0),
    SelectionItem("Price per 900 grams", 900.0),
    SelectionItem("Price per 800 grams", 800.0),
    SelectionItem("Price per 750 grams", 750.0),
    SelectionItem("Price per 700 grams", 700.0),
    SelectionItem("Price per 600 grams", 600.0),
    SelectionItem("Price per 500 grams", 500.0),
    SelectionItem("Price per 400 grams", 400.0),
    SelectionItem("Price per 250 grams", 250.0),
    SelectionItem("Price per 200 grams", 200.0),
    SelectionItem("Price per 100 grams", 100.0),
    SelectionItem("Price per gram", 1.0),
    SelectionItem("Price per milligram", 0.001),
    SelectionItem("Price per pound", 453.59237),
    SelectionItem("Price per 12 oz", 12.0 * 28.349523125),
    SelectionItem("Price per 8 oz", 8.0 * 28.349523125),
    SelectionItem("Price per 6 oz", 6.0 * 28.349523125),
    SelectionItem("Price per 4 oz", 4.0 * 28.349523125),
    SelectionItem("Price per 2 oz", 2.0 * 28.349523125),
    SelectionItem("Price per oz", 28.349523125),
)

val CONVERSION_UNITS = listOf(
    // Metric mass
    ConversionUnit("kg", 1.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 900.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 800.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 750.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 700.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 600.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 500.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 400.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 300.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 250.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 200.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 100.0, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 1.0, UnitCategory.METRIC_MASS),
    ConversionUnit("mg", 1.0, UnitCategory.METRIC_MASS),
    // Imperial mass
    ConversionUnit("lbs", 3.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("lbs", 1.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 12.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 8.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 6.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 4.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 2.0, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 1.0, UnitCategory.IMPERIAL_MASS),
    // Metric volume
    ConversionUnit("l", 1.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 900.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 800.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 700.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 600.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 500.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 400.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 300.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 200.0, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 100.0, UnitCategory.METRIC_VOLUME),
    // Imperial volume
    ConversionUnit("gallon", 1.0, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("gallon", 0.5, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("quart", 1.0, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("pint", 1.0, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("fluid_ounce", 1.0, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("cup", 1.0, UnitCategory.IMPERIAL_VOLUME),
)
