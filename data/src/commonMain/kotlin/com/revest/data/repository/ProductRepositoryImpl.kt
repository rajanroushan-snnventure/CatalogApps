package com.revest.data.repository

import com.revest.core.common.AppDispatchers
import com.revest.data.remote.ProductApiService
import com.revest.data.remote.toDomain
import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage
import com.revest.domain.repository.ProductRepository
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val api: ProductApiService
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): Result<ProductsPage> =
        withContext(AppDispatchers.io) {
            runCatching { api.getProducts(limit, skip).toDomain() }
        }

    override suspend fun getProductById(id: Int): Result<Product> =
        withContext(AppDispatchers.io) {
            runCatching { api.getProductById(id).toDomain() }
        }

    override suspend fun searchProducts(query: String): Result<ProductsPage> =
        withContext(AppDispatchers.io) {
            runCatching { api.searchProducts(query).toDomain() }
        }

    override suspend fun getProductsByCategory(category: String): Result<ProductsPage> =
        withContext(AppDispatchers.io) {
            runCatching { api.getProductsByCategory(category).toDomain() }
        }

    override suspend fun getCategories(): Result<List<String>> =
        withContext(AppDispatchers.io) {
            runCatching { api.getCategories() }
        }
}
