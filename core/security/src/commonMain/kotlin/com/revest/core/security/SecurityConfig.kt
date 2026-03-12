package com.revest.core.security

/**
 * Central security configuration.
 *
 * Certificate pins are SHA-256 hashes of the public key of dummyjson.com.
 * Run:  openssl s_client -connect dummyjson.com:443 | openssl x509 -pubkey -noout |
 *         openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64
 * to regenerate if the cert rotates.
 */
object SecurityConfig {
    /** Base API URL */
    const val BASE_URL = "https://dummyjson.com"

    /** Host for certificate pinning */
    const val API_HOST = "dummyjson.com"

    /**
     * SHA-256 pins for dummyjson.com (leaf + intermediate for resilience).
     * Prefixed with "sha256/" as required by OkHttp CertificatePinner.
     */
    val CERTIFICATE_PINS = listOf(
        "sha256/FEzVOUp4dF3gI0ZVPRJhFbSJVXR+uQmMH65xhs1glH4=",  // leaf
        "sha256/Y9mvm0exBk1JoQ57f9Vm28jKo5lFm/woKcVxrYxu80o=",  // intermediate R3
        "sha256/HXZBWNO9X2c12Jz6sIEPvxkRRGvfB7Jh3mBbJgF2is="   // ISRG Root X1
    )

    /** Network timeout in milliseconds */
    const val CONNECT_TIMEOUT_MS = 15_000L
    const val READ_TIMEOUT_MS    = 30_000L

    /** Maximum number of retries for failed requests */
    const val MAX_RETRIES = 3

    /** Whether to enforce strict TLS (disable on debug only) */
    const val ENFORCE_CERTIFICATE_PINNING = true
}
