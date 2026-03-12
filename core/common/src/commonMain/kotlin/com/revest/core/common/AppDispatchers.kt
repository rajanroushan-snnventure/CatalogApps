package com.revest.core.common

import kotlinx.coroutines.CoroutineDispatcher

expect object AppDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}
