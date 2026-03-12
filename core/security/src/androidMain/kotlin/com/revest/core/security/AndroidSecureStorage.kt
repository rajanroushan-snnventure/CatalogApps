package com.revest.core.security

import com.russhwolf.settings.Settings

/**
 * Android implementation using multiplatform-settings backed by SharedPreferences.
 * In production wire to EncryptedSharedPreferences via a custom Settings factory.
 */
class AndroidSecureStorage(private val settings: Settings) : SecureStorage {
    override fun putString(key: String, value: String) = settings.putString(key, value)
    override fun getString(key: String, defaultValue: String) =
        settings.getString(key, defaultValue)
    override fun remove(key: String) = settings.remove(key)
    override fun clear() = settings.clear()
}
