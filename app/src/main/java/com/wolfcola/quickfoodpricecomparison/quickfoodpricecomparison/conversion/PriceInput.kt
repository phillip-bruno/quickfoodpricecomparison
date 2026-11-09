package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion

import java.math.BigDecimal

sealed interface PriceInput {
    data class Valid(val value: Double) : PriceInput
    data class Invalid(val message: String) : PriceInput

    companion object {
        /**
         * The single place user-entered (or history-restored) price text is validated.
         * Note that String.toDoubleOrNull() happily accepts "NaN" and "Infinity", so finiteness
         * has to be checked explicitly.
         */
        fun parse(text: String): PriceInput {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) return Invalid("Enter a price")
            val value = trimmed.toDoubleOrNull()
            return when {
                value == null || !value.isFinite() -> Invalid("Not a valid number")
                value < 0 -> Invalid("Price can't be negative")
                else -> Valid(value)
            }
        }
    }
}

/**
 * Formats a price for display. Uses plain notation so small per-gram prices render as
 * "$0.00035" rather than Double.toString()'s "$3.5E-4", and drops trailing zeros.
 */
fun formatPrice(currency: String, value: Double): String =
    currency + BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
