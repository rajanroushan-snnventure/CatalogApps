package com.revest.domain

import com.revest.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    factory { GetProductsUseCase(get()) }
    factory { SearchProductsUseCase(get()) }
    factory { GetProductDetailUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetProductsByCategoryUseCase(get()) }
}
