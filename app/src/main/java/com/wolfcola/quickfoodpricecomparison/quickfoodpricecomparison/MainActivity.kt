package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.screens.MainScreen
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme.QuickFoodPriceComparisonTheme
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickFoodPriceComparisonTheme {
                val viewModel: MainViewModel = viewModel()
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
