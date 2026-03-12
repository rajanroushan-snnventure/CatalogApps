package com.revest.core.security

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * Android Koin module providing:
 *  - OkHttpClient with certificate pinning
 *  - Secure storage backed by SharedPreferences (swap to EncryptedSharedPreferences in prod)
 */
val androidSecurityModule = module {

    single<CertificatePinner> {
        CertificatePinner.Builder().apply {
            SecurityConfig.CERTIFICATE_PINS.forEach { pin ->
                add(SecurityConfig.API_HOST, pin)
            }
        }.build()
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .connectTimeout(SecurityConfig.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(SecurityConfig.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .apply {
                if (SecurityConfig.ENFORCE_CERTIFICATE_PINNING) {
                    certificatePinner(get())
                }
            }
            .build()
    }

    // Secure storage — replace SharedPreferencesSettings with EncryptedSharedPreferencesSettings for prod
    single<SecureStorage> {
        val context = get<android.content.Context>()
        val prefs = context.getSharedPreferences("revest_secure", android.content.Context.MODE_PRIVATE)
        AndroidSecureStorage(SharedPreferencesSettings(prefs))
    }
}
