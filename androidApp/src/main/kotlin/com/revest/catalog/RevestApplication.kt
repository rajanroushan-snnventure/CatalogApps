package com.revest.catalog

import android.app.Application
import android.content.Context
import com.revest.core.network.networkModule
import com.revest.core.security.androidSecurityModule
import com.revest.core.security.securityModule
import com.revest.data.di.dataModule
import com.revest.domain.domainModule
import com.revest.feature.productdetail.di.productDetailModule
import com.revest.feature.productlist.di.productListModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class RevestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Wire Android context
            androidContext(this@RevestApplication)
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)

            modules(
                // ── Platform ─────────────────────────────────────────────────
                androidContextModule(this@RevestApplication),
                securityModule,
                androidSecurityModule,

                // ── Network ──────────────────────────────────────────────────
                networkModule,

                // ── Data ─────────────────────────────────────────────────────
                dataModule,

                // ── Domain ───────────────────────────────────────────────────
                domainModule,

                // ── Features ─────────────────────────────────────────────────
                productListModule,
                productDetailModule
            )
        }
    }
}

/**
 * Provides the Android [Context] as a Koin definition so modules
 * (e.g. [androidSecurityModule]) can inject it without coupling to Android.
 */
fun androidContextModule(context: Context) = module {
    single<Context> { context }
}
