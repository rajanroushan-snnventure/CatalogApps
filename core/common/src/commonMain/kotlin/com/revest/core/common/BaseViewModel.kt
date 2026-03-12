package com.revest.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// ── State Delegate ────────────────────────────────────────────────────────────
/**
 * Property delegate that backs a public [StateFlow] with a private [MutableStateFlow].
 *
 * Usage:
 *   private val _stateDelegate = stateDelegate(MyState())
 *   val state: StateFlow<MyState> by _stateDelegate
 *   // update via: _stateDelegate.update { it.copy(...) }
 */
class StateDelegate<S>(initialState: S) : ReadOnlyProperty<Any?, StateFlow<S>> {

    private val _flow = MutableStateFlow(initialState)
    val flow: StateFlow<S> = _flow.asStateFlow()

    /** Atomically transform the current state. */
    fun update(transform: (S) -> S) {
        _flow.value = transform(_flow.value)
    }

    /** Directly set a new state. */
    fun set(newState: S) {
        _flow.value = newState
    }

    /** Returns the current snapshot. */
    val current: S get() = _flow.value

    override fun getValue(thisRef: Any?, property: KProperty<*>): StateFlow<S> = flow
}

fun <S> stateDelegate(initialState: S) = StateDelegate(initialState)

// ── Base ViewModel ────────────────────────────────────────────────────────────
/**
 * Platform-agnostic ViewModel base class.
 * Subclasses call [launch] for coroutine work and [onCleared] on lifecycle end.
 */
abstract class BaseViewModel {

    protected val viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + AppDispatchers.main)

    open fun onCleared() {
        viewModelScope.cancel()
    }

    protected fun launch(
        dispatcher: kotlinx.coroutines.CoroutineDispatcher = AppDispatchers.main,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(dispatcher, block = block)
}
