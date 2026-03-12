package com.revest.core.security

import com.russhwolf.settings.Settings

/**
 * iOS implementation — multiplatform-settings uses NSUserDefaults by default.
 * For production Keychain usage, supply a KeychainSettings from the library.
 */
class IosSecureStorage(private val settings: Settings) : SecureStorage {
    override fun putString(key: String, value: String) = settings.putString(key, value)
    override fun getString(key: String, defaultValue: String) =
        settings.getString(key, defaultValue)
    override fun remove(key: String) = settings.remove(key)
    override fun clear() = settings.clear()
}
