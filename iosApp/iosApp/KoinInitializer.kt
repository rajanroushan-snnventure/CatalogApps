package com.revest.catalog

import com.revest.core.network.networkModule
import com.revest.core.security.securityModule
import com.revest.data.di.dataModule
import com.revest.domain.domainModule
import com.revest.feature.productdetail.di.productDetailModule
import com.revest.feature.productlist.di.productListModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            securityModule,
            networkModule,
            dataModule,
            domainModule,
            productListModule,
            productDetailModule
        )
    }
}
