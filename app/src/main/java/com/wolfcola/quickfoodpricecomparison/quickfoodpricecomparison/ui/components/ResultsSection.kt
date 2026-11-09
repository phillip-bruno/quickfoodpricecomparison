package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.conversion.formatPrice
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data.CONVERSION_UNITS
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.ConversionUnit
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.UnitCategory

private val TAB_CATEGORIES = listOf(
    "Metric Mass" to UnitCategory.METRIC_MASS,
    "American Mass" to UnitCategory.IMPERIAL_MASS,
    "Metric Volume" to UnitCategory.METRIC_VOLUME,
    "American Volume" to UnitCategory.IMPERIAL_VOLUME,
)

@Composable
fun ResultsSection(currency: String, prices: Map<ConversionUnit, Double>) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Results",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            TAB_CATEGORIES.forEachIndexed { index, (title, _) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, maxLines = 1) }
                )
            }
        }

        val category = TAB_CATEGORIES[selectedTab].second
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            CONVERSION_UNITS.filter { it.category == category }.forEach { unit ->
                prices[unit]?.let { value ->
                    ResultRow(label = unit.displayLabel(), value = formatPrice(currency, value))
                }
            }
        }
    }
}
