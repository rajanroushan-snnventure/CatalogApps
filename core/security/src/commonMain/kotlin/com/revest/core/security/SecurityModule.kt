package com.revest.core.security

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val securityModule = module {
    // Platform-agnostic Settings (no-arg constructor picks platform default)
    single<Settings> { Settings() }
}
