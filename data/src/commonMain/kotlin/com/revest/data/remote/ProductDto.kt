package com.revest.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String? = null,
    val category: String,
    val thumbnail: String,
    val images: List<String> = emptyList()
)

@Serializable
data class ProductsPageDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
