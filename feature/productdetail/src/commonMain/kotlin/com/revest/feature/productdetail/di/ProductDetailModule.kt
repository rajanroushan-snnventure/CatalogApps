package com.revest.feature.productdetail.di

import com.revest.feature.productdetail.presentation.ProductDetailViewModel
import org.koin.dsl.module

val productDetailModule = module {
    factory { ProductDetailViewModel(getProductDetailUseCase = get()) }
}
