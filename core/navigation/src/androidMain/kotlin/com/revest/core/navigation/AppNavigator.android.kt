package com.revest.core.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Concrete [Navigator] that emits [NavigationCommand]s into a [SharedFlow].
 * The [AppNavHost] Composable collects these and calls the NavController.
 */
class AppNavigator : Navigator {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _commands = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 16)
    override val commands: SharedFlow<NavigationCommand> = _commands.asSharedFlow()

    override fun navigate(route: String, popUpTo: String?, inclusive: Boolean) {
        scope.launch {
            _commands.emit(
                NavigationCommand.NavigateTo(
                    route = route,
                    popUpTo = popUpTo,
                    inclusive = inclusive
                )
            )
        }
    }

    override fun navigateBack() {
        scope.launch { _commands.emit(NavigationCommand.Back) }
    }
}
