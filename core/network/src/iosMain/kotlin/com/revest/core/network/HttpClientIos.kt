package com.revest.core.network

import com.revest.core.security.SecureStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClient(secureStorage: SecureStorage): HttpClient =
    buildHttpClient(
        engine = Darwin.create(),
        secureStorage = secureStorage
    )
