package com.revest.domain.usecase

import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage
import com.revest.domain.repository.ProductRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.*

// ── Fake Repository ───────────────────────────────────────────────────────────
class FakeProductRepository : ProductRepository {
    var productsResult: Result<ProductsPage> = Result.success(emptyPage())
    var searchResult: Result<ProductsPage>   = Result.success(emptyPage())
    var detailResult: Result<Product>        = Result.success(sampleProduct())
    var categoriesResult: Result<List<String>> = Result.success(listOf("phones", "laptops"))

    var lastLimit  = -1
    var lastSkip   = -1
    var lastQuery  = ""
    var lastCategory = ""

    override suspend fun getProducts(limit: Int, skip: Int): Result<ProductsPage> {
        lastLimit = limit; lastSkip = skip
        return productsResult
    }
    override suspend fun getProductById(id: Int) = detailResult
    override suspend fun searchProducts(query: String): Result<ProductsPage> {
        lastQuery = query; return searchResult
    }
    override suspend fun getProductsByCategory(category: String): Result<ProductsPage> {
        lastCategory = category; return productsResult
    }
    override suspend fun getCategories() = categoriesResult

    companion object {
        fun emptyPage() = ProductsPage(emptyList(), 0, 0, 20)
        fun samplePage(count: Int = 3) = ProductsPage(
            products = (1..count).map { sampleProduct(it) },
            total = count, skip = 0, limit = 20
        )
        fun sampleProduct(id: Int = 1) = Product(
            id = id, title = "Product $id", description = "Desc $id",
            price = 99.99 * id, discountPercentage = 10.0, rating = 4.5,
            stock = 100, brand = "Brand", category = "phones",
            thumbnail = "https://example.com/$id.jpg", images = emptyList()
        )
    }
}

// ── GetProductsUseCase Tests ──────────────────────────────────────────────────
class GetProductsUseCaseTest {

    @Test fun `returns success with products from repository`() = runTest {
        val repo = FakeProductRepository().apply { productsResult = Result.success(FakeProductRepository.samplePage(5)) }
        val result = GetProductsUseCase(repo)(limit = 20, skip = 0)

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull()?.products?.size)
    }

    @Test fun `propagates failure from repository`() = runTest {
        val repo = FakeProductRepository().apply {
            productsResult = Result.failure(RuntimeException("Network error"))
        }
        val result = GetProductsUseCase(repo)()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test fun `passes correct pagination params to repository`() = runTest {
        val repo = FakeProductRepository()
        GetProductsUseCase(repo)(limit = 10, skip = 40)

        assertEquals(10, repo.lastLimit)
        assertEquals(40, repo.lastSkip)
    }
}

// ── SearchProductsUseCase Tests ───────────────────────────────────────────────
class SearchProductsUseCaseTest {

    @Test fun `returns failure for blank query`() = runTest {
        val repo = FakeProductRepository()
        val result = SearchProductsUseCase(repo)("   ")

        assertTrue(result.isFailure)
        assertIs<IllegalArgumentException>(result.exceptionOrNull())
    }

    @Test fun `trims whitespace before forwarding query`() = runTest {
        val repo = FakeProductRepository()
        SearchProductsUseCase(repo)("  phone  ")

        assertEquals("phone", repo.lastQuery)
    }

    @Test fun `returns search results on success`() = runTest {
        val page = FakeProductRepository.samplePage(2)
        val repo = FakeProductRepository().apply { searchResult = Result.success(page) }
        val result = SearchProductsUseCase(repo)("phone")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.products?.size)
    }
}

// ── GetProductDetailUseCase Tests ─────────────────────────────────────────────
class GetProductDetailUseCaseTest {

    @Test fun `returns product on success`() = runTest {
        val product = FakeProductRepository.sampleProduct(7)
        val repo = FakeProductRepository().apply { detailResult = Result.success(product) }
        val result = GetProductDetailUseCase(repo)(7)

        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull()?.id)
    }

    @Test fun `propagates error from repository`() = runTest {
        val repo = FakeProductRepository().apply {
            detailResult = Result.failure(RuntimeException("Not found"))
        }
        val result = GetProductDetailUseCase(repo)(99)

        assertTrue(result.isFailure)
    }
}

// ── Product Domain Model Tests ────────────────────────────────────────────────
class ProductModelTest {

    @Test fun `discountedPrice calculates correctly`() {
        val product = FakeProductRepository.sampleProduct().copy(price = 100.0, discountPercentage = 20.0)
        assertEquals(80.0, product.discountedPrice, 0.001)
    }

    @Test fun `discountedPrice returns original when no discount`() {
        val product = FakeProductRepository.sampleProduct().copy(price = 50.0, discountPercentage = 0.0)
        assertEquals(50.0, product.discountedPrice, 0.001)
    }

    @Test fun `ProductsPage hasMore is correct`() {
        val page = ProductsPage(
            products = (1..10).map { FakeProductRepository.sampleProduct(it) },
            total = 100, skip = 0, limit = 10
        )
        assertTrue(page.hasMore)

        val lastPage = page.copy(skip = 90)
        assertFalse(lastPage.hasMore)
    }
}
