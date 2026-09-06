package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.SelectionItem
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.UnitSymbol

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
    ConversionUnit(UnitSymbol.KG, 1.0),
    ConversionUnit(UnitSymbol.G, 900.0),
    ConversionUnit(UnitSymbol.G, 800.0),
    ConversionUnit(UnitSymbol.G, 750.0),
    ConversionUnit(UnitSymbol.G, 700.0),
    ConversionUnit(UnitSymbol.G, 600.0),
    ConversionUnit(UnitSymbol.G, 500.0),
    ConversionUnit(UnitSymbol.G, 400.0),
    ConversionUnit(UnitSymbol.G, 300.0),
    ConversionUnit(UnitSymbol.G, 250.0),
    ConversionUnit(UnitSymbol.G, 200.0),
    ConversionUnit(UnitSymbol.G, 100.0),
    ConversionUnit(UnitSymbol.G, 1.0),
    ConversionUnit(UnitSymbol.MG, 1.0),
    // Imperial mass
    ConversionUnit(UnitSymbol.LBS, 3.0),
    ConversionUnit(UnitSymbol.LBS, 1.0),
    ConversionUnit(UnitSymbol.OZ, 12.0),
    ConversionUnit(UnitSymbol.OZ, 8.0),
    ConversionUnit(UnitSymbol.OZ, 6.0),
    ConversionUnit(UnitSymbol.OZ, 4.0),
    ConversionUnit(UnitSymbol.OZ, 2.0),
    ConversionUnit(UnitSymbol.OZ, 1.0),
    // Metric volume
    ConversionUnit(UnitSymbol.L, 1.0),
    ConversionUnit(UnitSymbol.ML, 900.0),
    ConversionUnit(UnitSymbol.ML, 800.0),
    ConversionUnit(UnitSymbol.ML, 700.0),
    ConversionUnit(UnitSymbol.ML, 600.0),
    ConversionUnit(UnitSymbol.ML, 500.0),
    ConversionUnit(UnitSymbol.ML, 400.0),
    ConversionUnit(UnitSymbol.ML, 300.0),
    ConversionUnit(UnitSymbol.ML, 200.0),
    ConversionUnit(UnitSymbol.ML, 100.0),
    // Imperial volume
    ConversionUnit(UnitSymbol.GALLON, 1.0),
    ConversionUnit(UnitSymbol.GALLON, 0.5),
    ConversionUnit(UnitSymbol.QUART, 1.0),
    ConversionUnit(UnitSymbol.PINT, 1.0),
    ConversionUnit(UnitSymbol.FLUID_OUNCE, 1.0),
    ConversionUnit(UnitSymbol.CUP, 1.0),
)
