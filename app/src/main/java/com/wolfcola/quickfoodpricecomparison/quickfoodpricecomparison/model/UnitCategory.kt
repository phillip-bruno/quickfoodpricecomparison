package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model

enum class UnitCategory {
    METRIC_MASS,
    IMPERIAL_MASS,
    METRIC_VOLUME,
    IMPERIAL_VOLUME;

    val isMass: Boolean
        get() = this == METRIC_MASS || this == IMPERIAL_MASS

    val isVolume: Boolean
        get() = this == METRIC_VOLUME || this == IMPERIAL_VOLUME
}
