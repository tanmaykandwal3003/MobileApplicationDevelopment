package com.example.currencycalc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencycalc.ui.navigation.NavRoutes
import com.example.currencycalc.viewmodel.CurrencyViewModel
import com.example.currencycalc.viewmodel.ThemeViewModel

@Composable
fun AppNavHost(
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.CONVERTER,
        modifier = modifier
    ) {
        composable(NavRoutes.CONVERTER) {
            val currencyViewModel: CurrencyViewModel = viewModel()
            CurrencyConverterScreen(
                viewModel = currencyViewModel,
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
