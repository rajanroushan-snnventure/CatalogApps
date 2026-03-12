package com.revest.data.di

import com.revest.data.remote.ProductApiService
import com.revest.data.repository.ProductRepositoryImpl
import com.revest.domain.repository.ProductRepository
import org.koin.dsl.module

val dataModule = module {
    single { ProductApiService(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
}
