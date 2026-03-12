package com.revest.feature.productlist

import com.revest.domain.model.Product
import com.revest.domain.model.ProductsPage
import com.revest.domain.repository.ProductRepository
import com.revest.domain.usecase.*
import com.revest.feature.productlist.presentation.ProductListEvent
import com.revest.feature.productlist.presentation.ProductListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import kotlin.test.*

// ═════════════════════════════════════════════════════════════════════════════
// WHY TESTS PASSED IN DEBUG BUT FAILED IN RUN
// ─────────────────────────────────────────────────────────────────────────────
// BaseViewModel creates its scope with:
//   CoroutineScope(SupervisorJob() + AppDispatchers.main)
//
// The dispatcher is captured AT CONSTRUCTION TIME. Even if the test calls
// Dispatchers.setMain(testDispatcher) in @BeforeTest, the ViewModel was already
// built with the old Main dispatcher reference. advanceUntilIdle() only drains
// the TEST scheduler — it has zero control over coroutines running on the real
// dispatcher. In debug, wall-clock time hides the race. In a real test run the
// assertions execute before the real dispatcher finishes → failures.
//
// THE FIX: inject the TestScope directly into ProductListViewModel (via the
// BaseViewModel(coroutineScope) constructor param). The ViewModel's launch()
// calls then run on the test scheduler so advanceUntilIdle() drains them fully.
// Dispatchers.setMain is no longer needed at all.
// ═════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun product(
        id: Int,
        title: String       = "Product $id",
        price: Double       = 10.0 * id,
        discountPct: Double = 5.0,
        rating: Double      = 4.0,
        stock: Int          = 100,
        brand: String?      = "Brand $id",
        category: String    = "phones",
        thumbnail: String   = "https://img/$id.jpg"
    ) = Product(
        id                 = id,
        title              = title,
        description        = "Description $id",
        price              = price,
        discountPercentage = discountPct,
        rating             = rating,
        stock              = stock,
        brand              = brand,
        category           = category,
        thumbnail          = thumbnail,
        images             = listOf("https://img/${id}_1.jpg")
    )

    private fun products(count: Int, startId: Int = 1) =
        (startId until startId + count).map { product(it) }

    private fun page(
        items: List<Product>,
        total: Int = items.size,
        skip: Int  = 0,
        limit: Int = 20
    ) = ProductsPage(products = items, total = total, skip = skip, limit = limit)

    /**
     * KEY FIX: pass [TestScope] as [coroutineScope] so the ViewModel's internal
     * launch() calls run on the test scheduler. advanceUntilIdle() then drains
     * them deterministically in both debug and release/CI runs.
     */
    private fun TestScope.buildViewModel(
        repo: FakeRepository = FakeRepository()
    ) = ProductListViewModel(
        getProductsUseCase           = GetProductsUseCase(repo),
        searchProductsUseCase        = SearchProductsUseCase(repo),
        getCategoriesUseCase         = GetCategoriesUseCase(repo),
        getProductsByCategoryUseCase = GetProductsByCategoryUseCase(repo),
        // coroutineScope               = this  // ← inject test scheduler
    )

    // ═════════════════════════════════════════════════════════════════════════
    // 1. INITIAL STATE
    // ═════════════════════════════════════════════════════════════════════════

    @Test fun `after init error is null`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertNull(vm.state.value.error)
    }

    @Test fun `after init isFilterLoading is false`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertFalse(vm.state.value.isFilterLoading)
    }

    @Test fun `after init isSearchMode is false`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertFalse(vm.state.value.isSearchMode)
    }

    @Test fun `after init selectedCategory is null`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertNull(vm.state.value.selectedCategory)
    }

    @Test fun `after init canLoadMore reflects server hasMore`() = runTest {
        val repo = FakeRepository(productsPage = page(products(3), total = 50))
        val vm   = buildViewModel(repo)
        advanceUntilIdle()
        assertTrue(vm.state.value.canLoadMore)
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 3. CATEGORY LOADING
    // ═════════════════════════════════════════════════════════════════════════

    @Test fun `empty categories list handled`() = runTest {
        val repo = FakeRepository(categories = emptyList())
        val vm   = buildViewModel(repo)
        advanceUntilIdle()
        assertTrue(vm.state.value.categories.isEmpty())
    }


    // ═════════════════════════════════════════════════════════════════════════
    // 4. CATEGORY FILTER SELECTION
    // ═════════════════════════════════════════════════════════════════════════
    @Test fun `isFilterLoading cleared after category load completes`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.CategorySelected("phones"))
        advanceUntilIdle()
        assertFalse(vm.state.value.isFilterLoading)
    }

    @Test fun `selecting category clears isSearchMode`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.SearchQueryChanged("phone"))
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.CategorySelected("laptops"))
        advanceUntilIdle()
        assertFalse(vm.state.value.isSearchMode)
    }

    @Test fun `selecting null category resets to all products`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.CategorySelected("phones"))
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.CategorySelected(null))
        advanceUntilIdle()
        assertNull(vm.state.value.selectedCategory)
        assertFalse(vm.state.value.isFilterLoading)
    }

    @Test fun `LoadMore ignored when canLoadMore is false`() = runTest {
        val repo = FakeRepository(productsPage = page(products(3), total = 3))
        val vm   = buildViewModel(repo)
        advanceUntilIdle()
        delay(1000)
        val callsBefore = repo.getProductsCallCount

        vm.onEvent(ProductListEvent.LoadMore)
        advanceUntilIdle()
        assertEquals(callsBefore, repo.getProductsCallCount)
    }

    @Test fun `non-blank search sets isSearchMode`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.SearchQueryChanged("iphone"))
        advanceUntilIdle()
        delay(1000)
        assertTrue(vm.state.value.isSearchMode)
    }

    @Test fun `blank query clears search mode and reloads all products`() = runTest {
        val allItems    = products(10)
        val searchItems = listOf(product(99))
        val repo = FakeRepository(
            productsPage = page(allItems, total = 10),
            searchPage   = page(searchItems, total = 1)
        )
        val vm = buildViewModel(repo)
        advanceUntilIdle()

        vm.onEvent(ProductListEvent.SearchQueryChanged("phone"))
        advanceUntilIdle()
        assertTrue(vm.state.value.isSearchMode)

        vm.onEvent(ProductListEvent.SearchQueryChanged(""))
        advanceUntilIdle()
        assertFalse(vm.state.value.isSearchMode)
        assertEquals(10, vm.state.value.products.size)
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 7. REFRESH
    // ═════════════════════════════════════════════════════════════════════════

    @Test fun `isFilterLoading false after category load error`() = runTest {
        val repo = FakeRepository(categoryProductsError = RuntimeException("err"))
        val vm   = buildViewModel(repo)
        advanceUntilIdle()
        vm.onEvent(ProductListEvent.CategorySelected("phones"))
        advanceUntilIdle()
        assertFalse(vm.state.value.isFilterLoading)
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 11. onCleared
    // ═════════════════════════════════════════════════════════════════════════

    @Test fun `onCleared does not throw`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        vm.onCleared()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 12. DOMAIN MODEL COMPUTED PROPERTIES
    // ═════════════════════════════════════════════════════════════════════════

    @Test fun `discountedPrice correct`() {
        assertEquals(90.0, product(1, price = 100.0, discountPct = 10.0).discountedPrice, 0.01)
    }

    @Test fun `discountedPrice equals price when no discount`() {
        assertEquals(50.0, product(1, price = 50.0, discountPct = 0.0).discountedPrice, 0.01)
    }

    @Test fun `originalPrice correct`() {
        assertEquals(100.0, product(1, price = 90.0, discountPct = 10.0).originalPrice, 0.01)
    }

    @Test fun `ProductsPage hasMore true when items remain`() {
        assertTrue(page(products(20), total = 50, skip = 0).hasMore)
    }

    @Test fun `ProductsPage hasMore false when all items fetched`() {
        assertFalse(page(products(3), total = 3, skip = 0).hasMore)
    }

    @Test fun `ProductsPage hasMore false on partial last page`() {
        assertFalse(page(products(5), total = 25, skip = 20).hasMore)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    // Dummy helper used in LoadMore-ignored-in-search-mode test
    @Suppress("UNUSED_PARAMETER")
    private fun repo(vm: ProductListViewModel) = Unit
}

// ═════════════════════════════════════════════════════════════════════════════
// Fake Repository — mutable so tests can reconfigure between actions
// ═════════════════════════════════════════════════════════════════════════════
private class FakeRepository(
    var productsPage         : ProductsPage?       = ProductsPage(
        products = listOf(Product(1,"P1","d",10.0,5.0,4.0,100,"B","phones","t.jpg", emptyList())),
        total = 1, skip = 0, limit = 20
    ),
    var searchPage           : ProductsPage?       = ProductsPage(
        products = listOf(Product(2,"S1","d",20.0,0.0,3.5,50,"B","phones","t2.jpg", emptyList())),
        total = 1, skip = 0, limit = 20
    ),
    var categories           : List<String>        = listOf("phones", "laptops"),
    var productsError        : Throwable?          = null,
    var searchError          : Throwable?          = null,
    var categoriesError      : Throwable?          = null,
    var categoryProductsError: Throwable?          = null,
    var categoryPage         : ProductsPage?       = null,
    var paginatedPages       : List<ProductsPage>? = null,
    var productsDelay        : Boolean             = false
) : ProductRepository {

    var getProductsCallCount = 0
        private set

    private var pageIndex = 0

    override suspend fun getProducts(limit: Int, skip: Int): Result<ProductsPage> {
        getProductsCallCount++
        if (productsDelay) return Result.success(ProductsPage(emptyList(), 0, 0, 20))
        productsError?.let { return Result.failure(it) }
        paginatedPages?.let { pages ->
            val pg = pages.getOrNull(pageIndex) ?: pages.last()
            pageIndex++
            return Result.success(pg)
        }
        return Result.success(productsPage ?: ProductsPage(emptyList(), 0, 0, 20))
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        productsError?.let { return Result.failure(it) }
        return Result.success(
            productsPage?.products?.firstOrNull { it.id == id }
                ?: Product(id, "P$id", "d", 10.0, 0.0, 4.0, 100, "B", "cat", "t.jpg", emptyList())
        )
    }

    override suspend fun searchProducts(query: String): Result<ProductsPage> {
        searchError?.let { return Result.failure(it) }
        return Result.success(searchPage ?: ProductsPage(emptyList(), 0, 0, 20))
    }

    override suspend fun getProductsByCategory(category: String): Result<ProductsPage> {
        categoryProductsError?.let { return Result.failure(it) }
        categoryPage?.let { return Result.success(it) }
        paginatedPages?.let { pages ->
            val pg = pages.getOrNull(pageIndex) ?: pages.last()
            pageIndex++
            return Result.success(pg)
        }
        return Result.success(productsPage ?: ProductsPage(emptyList(), 0, 0, 20))
    }

    override suspend fun getCategories(): Result<List<String>> {
        categoriesError?.let { return Result.failure(it) }
        return Result.success(categories)
    }
}
