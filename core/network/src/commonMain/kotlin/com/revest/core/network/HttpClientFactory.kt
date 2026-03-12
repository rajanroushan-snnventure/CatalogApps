package com.revest.core.network

import com.revest.core.security.SecureStorage
import com.revest.core.security.SecureStorageKeys
import com.revest.core.security.SecurityConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun buildHttpClient(
    engine: HttpClientEngine,
    secureStorage: SecureStorage
): HttpClient = HttpClient(engine) {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient         = true
            coerceInputValues = true
        })
    }

    install(HttpTimeout) {
        connectTimeoutMillis = SecurityConfig.CONNECT_TIMEOUT_MS
        requestTimeoutMillis = SecurityConfig.READ_TIMEOUT_MS
        socketTimeoutMillis  = SecurityConfig.READ_TIMEOUT_MS
    }

    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = SecurityConfig.MAX_RETRIES)
        retryOnException(maxRetries = SecurityConfig.MAX_RETRIES, retryOnTimeout = true)
        exponentialDelay()
        modifyRequest { request ->
            request.headers.append("X-Retry-Count", retryCount.toString())
        }
    }

    defaultRequest {
        url(SecurityConfig.BASE_URL)
        contentType(ContentType.Application.Json)
        val token = secureStorage.getString(SecureStorageKeys.AUTH_TOKEN)
        if (token.isNotBlank()) {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
        headers.append("X-Client-Version", "1.0.0")
        headers.append("X-Platform", "revest-mobile")
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level  = LogLevel.INFO
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}
