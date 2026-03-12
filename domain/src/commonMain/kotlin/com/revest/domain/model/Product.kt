package com.revest.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    val images: List<String>
) {
    /** Price after discount, rounded to 2 decimal places. */
    val discountedPrice: Double
        get() = if (discountPercentage > 0)
            price * (1 - discountPercentage / 100)
        else price

    /** Original price before discount. */
    val originalPrice: Double
        get() = if (discountPercentage > 0)
            price / (1 - discountPercentage / 100)
        else price
}

data class ProductsPage(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
) {
    val hasMore: Boolean get() = skip + products.size < total
}
