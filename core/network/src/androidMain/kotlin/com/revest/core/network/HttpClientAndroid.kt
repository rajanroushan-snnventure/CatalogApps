package com.revest.core.network

import com.revest.core.security.SecureStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createPlatformHttpClient(secureStorage: SecureStorage): HttpClient =
    buildHttpClient(
        engine = Android.create {
            connectTimeout = 15_000
            socketTimeout  = 30_000
        },
        secureStorage = secureStorage
    )
