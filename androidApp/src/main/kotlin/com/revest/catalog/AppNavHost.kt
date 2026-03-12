package com.revest.catalog

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.revest.core.navigation.AppRoute
import com.revest.core.navigation.NavigationCommand
import com.revest.core.navigation.Navigator
import com.revest.core.ui.RevestTheme
import com.revest.feature.productdetail.presentation.ProductDetailEvent
import com.revest.feature.productdetail.presentation.ProductDetailScreen
import com.revest.feature.productdetail.presentation.ProductDetailViewModel
import com.revest.feature.productlist.presentation.ProductListScreen
import com.revest.feature.productlist.presentation.ProductListViewModel
import com.revest.feature.splash.SplashScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoute.Splash.route
) {
    val navController = rememberNavController()

    // Collect navigation commands from the Navigator SharedFlow
    LaunchedEffect(navigator) {
        navigator.commands.collectLatest { cmd ->
            when (cmd) {
                is NavigationCommand.NavigateTo -> {
                    navController.navigate(cmd.route) {
                        cmd.popUpTo?.let { target ->
                            popUpTo(target) { inclusive = cmd.inclusive }
                        }
                    }
                }
                NavigationCommand.Back -> navController.popBackStack()
            }
        }
    }

    RevestTheme {
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = modifier
        ) {

            // ── Splash ──────────────────────────────────────────────────────
            composable(
                route      = AppRoute.Splash.route,
                deepLinks  = listOf(navDeepLink { uriPattern = AppRoute.Splash.deepLink })
            ) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(AppRoute.ProductList.route) {
                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Product List ────────────────────────────────────────────────
            composable(
                route      = AppRoute.ProductList.route,
                deepLinks  = listOf(navDeepLink { uriPattern = AppRoute.ProductList.deepLink })
            ) {
                val viewModel: ProductListViewModel = koinInject()
                val state by viewModel.state.collectAsState()

                DisposableEffect(Unit) { onDispose { viewModel.onCleared() } }

                ProductListScreen(
                    state          = state,
                    onEvent        = viewModel::onEvent,
                    onProductClick = { productId ->
                        navController.navigate(AppRoute.ProductDetail.createRoute(productId))
                    }
                )
            }

            // ── Product Detail ──────────────────────────────────────────────
            composable(
                route      = AppRoute.ProductDetail.route,
                arguments  = listOf(
                    navArgument(AppRoute.ARG_PRODUCT_ID) { type = NavType.IntType }
                ),
                deepLinks  = listOf(
                    navDeepLink { uriPattern = AppRoute.ProductDetail.deepLink }
                )
            ) { backStack ->
                val productId = backStack.arguments
                    ?.getInt(AppRoute.ARG_PRODUCT_ID) ?: return@composable

                val viewModel: ProductDetailViewModel = koinInject()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(productId) {
                    viewModel.onEvent(ProductDetailEvent.LoadProduct(productId))
                }
                DisposableEffect(Unit) { onDispose { viewModel.onCleared() } }

                ProductDetailScreen(
                    state   = state,
                    onEvent = viewModel::onEvent,
                    onBack  = { navController.popBackStack() }
                )
            }
        }
    }
}
