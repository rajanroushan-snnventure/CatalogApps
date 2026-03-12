package com.revest.domain.repository

import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage

interface ProductRepository {
    suspend fun getProducts(limit: Int = 20, skip: Int = 0): Result<ProductsPage>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun searchProducts(query: String): Result<ProductsPage>
    suspend fun getProductsByCategory(category: String): Result<ProductsPage>
    suspend fun getCategories(): Result<List<String>>
}
