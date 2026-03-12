package com.revest.core.security

/**
 * Platform-agnostic secure key-value storage.
 * Android → EncryptedSharedPreferences via multiplatform-settings
 * iOS     → Keychain via multiplatform-settings
 */
interface SecureStorage {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String
    fun remove(key: String)
    fun clear()
}

object SecureStorageKeys {
    const val AUTH_TOKEN   = "auth_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val USER_ID      = "user_id"
}
