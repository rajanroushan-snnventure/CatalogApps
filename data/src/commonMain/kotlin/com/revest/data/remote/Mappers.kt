package com.revest.data.remote

import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage

internal fun ProductDto.toDomain() = Product(
    id                 = id,
    title              = title,
    description        = description,
    price              = price,
    discountPercentage = discountPercentage,
    rating             = rating,
    stock              = stock,
    brand              = brand,
    category           = category,
    thumbnail          = thumbnail,
    images             = images
)

internal fun ProductsPageDto.toDomain() = ProductsPage(
    products = products.map { it.toDomain() },
    total    = total,
    skip     = skip,
    limit    = limit
)
