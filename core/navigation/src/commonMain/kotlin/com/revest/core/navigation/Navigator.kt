package com.revest.core.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Platform-agnostic navigator contract.
 * The Android implementation wires to [androidx.navigation.NavController].
 */
interface Navigator {
    /** Stream of navigation commands consumed by the NavHost. */
    val commands: SharedFlow<NavigationCommand>

    fun navigate(route: String, popUpTo: String? = null, inclusive: Boolean = false)
    fun navigateBack()
    fun navigateToProductList() = navigate(AppRoute.ProductList.route)
    fun navigateToProductDetail(productId: Int) =
        navigate(AppRoute.ProductDetail.createRoute(productId))
}

sealed interface NavigationCommand {
    data class NavigateTo(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : NavigationCommand
    object Back : NavigationCommand
}
