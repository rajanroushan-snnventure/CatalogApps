package com.revest.core.ui

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor2.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIApplication

/**
 * Initializes Coil's [SingletonImageLoader] for iOS.
 *
 * On Android, Coil auto-detects the OkHttp engine via ServiceLoader.
 * On iOS there is no Application context, so we must manually supply the
 * Darwin (URLSession-backed) Ktor engine and register the loader.
 *
 * Call this once from your Swift entry point — e.g. in [RevestCatalogApp.init()].
 * The KoinInitializer.kt already calls [initKoin]; add [initCoil] after that:
 *
 * ```swift
 * init() {
 *     KoinInitializerKt.doInitKoin()
 *     CoilInitializerKt.doInitCoil()   // <-- add this line
 * }
 * ```
 */
fun initCoil() {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(UIApplication.sharedApplication)
            .components {
                add(
                    KtorNetworkFetcherFactory(
                        httpClient = {
                            HttpClient(Darwin) {
                                engine {
                                    // URLSession configuration — tweak as needed
                                    configureSession { }
                                }
                            }
                        }
                    )
                )
            }
            .build()
    }
}
