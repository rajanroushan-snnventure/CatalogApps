package com.revest.domain.usecase

import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage
import com.revest.domain.repository.ProductRepository

// ── Get Products (paginated) ──────────────────────────────────────────────────
class GetProductsUseCase(private val repo: ProductRepository) {
    open suspend operator fun invoke(limit: Int = 20, skip: Int = 0): Result<ProductsPage> =
        repo.getProducts(limit = limit, skip = skip)
}

// ── Search Products ───────────────────────────────────────────────────────────
class SearchProductsUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(query: String): Result<ProductsPage> {
        if (query.isBlank())
            return Result.failure(IllegalArgumentException("Search query must not be blank"))
        return repo.searchProducts(query.trim())
    }
}

// ── Get Product Detail ────────────────────────────────────────────────────────
class GetProductDetailUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(id: Int): Result<Product> =
        repo.getProductById(id)
}

// ── Get Categories ────────────────────────────────────────────────────────────
class GetCategoriesUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(): Result<List<String>> =
        repo.getCategories()
}

// ── Get Products By Category ──────────────────────────────────────────────────
class GetProductsByCategoryUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(category: String, limit: Int, skip: Int ): Result<ProductsPage> =
        repo.getProductsByCategory(category)
}
