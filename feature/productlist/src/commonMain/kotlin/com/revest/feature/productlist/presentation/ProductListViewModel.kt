package com.revest.feature.productlist.presentation

import com.revest.core.common.BaseViewModel
import com.revest.core.common.AppDispatchers
import com.revest.core.common.stateDelegate
import com.revest.domain.model.Product
import com.revest.domain.usecase.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── State ─────────────────────────────────────────────────────────────────────
data class ProductListState(
    val products: List<Product>   = emptyList(),
    val isLoading: Boolean        = false,
    val isFilterLoading: Boolean  = false,
    val error: String?            = null,
    val searchQuery: String       = "",
    val selectedCategory: String? = null,
    val categories: List<String>  = emptyList(),
    val isSearchMode: Boolean     = false,
    val canLoadMore: Boolean      = true,
    // FIX: track skip directly instead of deriving it from currentPage * PAGE_SIZE.
    // currentPage was read from a stale snapshot `s` captured before the coroutine
    // ran, so page 3+ always re-sent skip=20. Using skip avoids the off-by-one.
    val currentSkip: Int          = 0,
    val totalProducts: Int        = 0
)

// ── Events ────────────────────────────────────────────────────────────────────
sealed class ProductListEvent {
    data class SearchQueryChanged(val query: String) : ProductListEvent()
    data class CategorySelected(val category: String?) : ProductListEvent()
    data class ProductClicked(val productId: Int) : ProductListEvent()
    object LoadMore     : ProductListEvent()
    object Refresh      : ProductListEvent()
    object DismissError : ProductListEvent()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : BaseViewModel() {

    private val _stateDelegate = stateDelegate(ProductListState(isLoading = true))
    val state: StateFlow<ProductListState> by _stateDelegate

    private val _searchFlow = MutableStateFlow("")

    init {
        loadCategories()
        loadProducts(reset = true)
        observeSearch()
    }

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.SearchQueryChanged -> {
                _stateDelegate.update { it.copy(searchQuery = event.query) }
                _searchFlow.value = event.query
                if(event.query.isNotEmpty()) {
                    _stateDelegate.update {
                        it.copy(
                            isSearchMode = true,
                            error = null
                        )
                    }
                } else {
                    _stateDelegate.update {
                        it.copy(
                            isSearchMode = false,
                            error = null
                        )
                    }
                }
            }
            is ProductListEvent.CategorySelected -> {
                _stateDelegate.update {
                    it.copy(
                        selectedCategory = event.category,
                        isFilterLoading  = false,
                        isLoading = false,
                        currentSkip      = 0,
                        canLoadMore      = true,
                        searchQuery      = "",
                        isSearchMode     = false,
                        error            = null
                    )
                }
                _searchFlow.value = ""
                loadProducts(reset = true)
            }
            ProductListEvent.LoadMore -> {
                val s = _stateDelegate.current
                if (!s.isLoading && !s.isFilterLoading && s.canLoadMore && !s.isSearchMode) {
                    loadProducts(reset = false)
                }
            }
            ProductListEvent.Refresh -> {
                _stateDelegate.update {
                    it.copy(
                        products    = emptyList(),
                        currentSkip = 0,
                        canLoadMore = true,
                        error       = null
                    )
                }
                loadProducts(reset = true)
            }
            ProductListEvent.DismissError ->
                _stateDelegate.update { it.copy(error = null) }
            is ProductListEvent.ProductClicked -> { /* handled by NavController */ }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        launch(AppDispatchers.default) {
            _searchFlow
                .debounce(400)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _stateDelegate.update {
                            it.copy(
                                isSearchMode    = false,
                                isFilterLoading = true,
                                currentSkip     = 0,
                                canLoadMore     = true
                            )
                        }
                        loadProducts(reset = true)
                    } else {
                        performSearch(query.trim())
                    }
                }
        }
    }

    private fun loadProducts(reset: Boolean) {
        // Read state ONCE inside the coroutine just before the network call
        // so we always have the freshest skip value.
        launch(AppDispatchers.io) {
            val s = _stateDelegate.current
            if (s.isSearchMode) return@launch

            _stateDelegate.update { it.copy(isLoading = true, error = null) }

            // FIX: use currentSkip from state (updated after each page) instead of
            // computing skip = currentPage * PAGE_SIZE from a stale outer snapshot.
            val skip = if (reset) 0 else _stateDelegate.current.currentSkip

            val result = if (s.selectedCategory != null) {
                // FIX: pass limit + skip so category results also paginate
                getProductsByCategoryUseCase(
                    category = s.selectedCategory,
                    limit    = PAGE_SIZE,
                    skip     = skip
                )
            } else {
                getProductsUseCase(limit = PAGE_SIZE, skip = skip)
            }

            result.fold(
                onSuccess = { page ->
                    _stateDelegate.update {
                        val merged   = if (reset) page.products else it.products + page.products
                        val nextSkip = skip + page.products.size
                        it.copy(
                            products        = merged,
                            totalProducts   = page.total,
                            isLoading       = false,
                            isFilterLoading = false,
                            // FIX: advance skip by the number of items actually returned,
                            // not by PAGE_SIZE, so the last partial page is handled correctly
                            currentSkip     = nextSkip,
                            canLoadMore     = page.hasMore,
                            isSearchMode    = false
                        )
                    }
                },
                onFailure = { err ->
                    _stateDelegate.update {
                        it.copy(
                            isLoading       = false,
                            isFilterLoading = false,
                            error           = err.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    private fun performSearch(query: String) {
        launch(AppDispatchers.io) {
            _stateDelegate.update {
                it.copy(isLoading = true, error = null, isSearchMode = true)
            }
            searchProductsUseCase(query).fold(
                onSuccess = { page ->
                    _stateDelegate.update {
                        it.copy(
                            products        = page.products,
                            totalProducts   = page.total,
                            isLoading       = false,
                            isFilterLoading = false,
                            canLoadMore     = false
                        )
                    }
                },
                onFailure = { err ->
                    _stateDelegate.update {
                        it.copy(
                            isLoading       = false,
                            isFilterLoading = false,
                            error           = err.message ?: "Search failed"
                        )
                    }
                }
            )
        }
    }

    private fun loadCategories() {
        launch(AppDispatchers.io) {
            getCategoriesUseCase().onSuccess { cats ->
                _stateDelegate.update { it.copy(categories = cats) }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
