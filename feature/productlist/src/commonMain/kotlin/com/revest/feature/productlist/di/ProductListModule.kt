package com.revest.feature.productlist.di

import com.revest.feature.productlist.presentation.ProductListViewModel
import org.koin.dsl.module

val productListModule = module {
    factory {
        ProductListViewModel(
            getProductsUseCase           = get(),
            searchProductsUseCase        = get(),
            getCategoriesUseCase         = get(),
            getProductsByCategoryUseCase = get()
        )
    }
}
