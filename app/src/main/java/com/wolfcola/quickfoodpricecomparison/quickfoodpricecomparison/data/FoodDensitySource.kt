package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity

/**
 * Seam between MainViewModel and where food density data actually comes from, so tests can
 * supply an in-memory fake instead of a real FoodDensityRepository (which needs a Context).
 */
interface FoodDensitySource {
    fun loadFoodDensities(): List<FoodDensity>
}
