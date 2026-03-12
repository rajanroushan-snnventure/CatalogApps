package com.revest.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import com.revest.core.security.SecurityConfig

class ProductApiService(private val client: HttpClient) {
    suspend fun getProducts(limit: Int, skip: Int): ProductsPageDto =
        client.get("${SecurityConfig.BASE_URL}/products") {
            parameter("limit", limit)
            parameter("skip",  skip)
        }.body()

    suspend fun getProductById(id: Int): ProductDto =
        client.get("${SecurityConfig.BASE_URL}/products/$id").body()

    suspend fun searchProducts(query: String): ProductsPageDto =
        client.get("${SecurityConfig.BASE_URL}/products/search") {
            parameter("q", query)
        }.body()

    suspend fun getProductsByCategory(category: String): ProductsPageDto =
        client.get("${SecurityConfig.BASE_URL}/products/category/$category").body()

    suspend fun getCategories(): List<String> =
        client.get("${SecurityConfig.BASE_URL}/products/category-list").body()
}
