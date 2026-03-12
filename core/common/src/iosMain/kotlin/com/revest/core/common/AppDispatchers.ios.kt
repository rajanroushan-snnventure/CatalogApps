package com.revest.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object AppDispatchers {
    actual val main: CoroutineDispatcher    = Dispatchers.Main
    actual val io: CoroutineDispatcher      = Dispatchers.Default
    actual val default: CoroutineDispatcher = Dispatchers.Default
}
