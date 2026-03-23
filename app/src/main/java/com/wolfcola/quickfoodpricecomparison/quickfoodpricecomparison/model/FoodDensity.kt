package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

data class FoodDensity(
    val foodName: String,
    val gMl: Double,
    val specificGravity: Double? = null,
    val biblioId: String = "",
    val category: String = ""
)
