package com.revest.core.network

import com.revest.core.security.SecureStorage
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 * expect declaration — tells the Kotlin compiler that each platform
 * (androidMain, iosMain) will supply an `actual` implementation.
 * Without this, commonMain cannot call createPlatformHttpClient at all.
 */
expect fun createPlatformHttpClient(secureStorage: SecureStorage): HttpClient

val networkModule = module {
    single<HttpClient> {
        createPlatformHttpClient(secureStorage = get())
    }
}
