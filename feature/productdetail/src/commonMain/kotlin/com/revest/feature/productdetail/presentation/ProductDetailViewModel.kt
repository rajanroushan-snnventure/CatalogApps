package com.revest.feature.productdetail.presentation

import com.revest.core.common.AppDispatchers
import com.revest.core.common.BaseViewModel
import com.revest.core.common.stateDelegate
import com.revest.domain.model.Product
import com.revest.domain.usecase.GetProductDetailUseCase
import kotlinx.coroutines.flow.StateFlow

// ── State ─────────────────────────────────────────────────────────────────────
data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

// ── Events ────────────────────────────────────────────────────────────────────
sealed class ProductDetailEvent {
    data class LoadProduct(val id: Int) : ProductDetailEvent()
    object DismissError : ProductDetailEvent()
    object NavigateBack : ProductDetailEvent()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase
) : BaseViewModel() {

    // Delegate-backed state
    private val _stateDelegate = stateDelegate(ProductDetailState())
    val state: StateFlow<ProductDetailState> by _stateDelegate

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.LoadProduct -> loadProduct(event.id)
            ProductDetailEvent.DismissError   -> _stateDelegate.update { it.copy(error = null) }
            ProductDetailEvent.NavigateBack   -> { /* handled by NavController */ }
        }
    }

    private fun loadProduct(id: Int) {
        launch(AppDispatchers.io) {
            _stateDelegate.update { it.copy(isLoading = true, error = null) }
            getProductDetailUseCase(id).fold(
                onSuccess = { product ->
                    _stateDelegate.update { it.copy(product = product, isLoading = false) }
                },
                onFailure = { err ->
                    _stateDelegate.update {
                        it.copy(isLoading = false, error = err.message ?: "Failed to load product")
                    }
                }
            )
        }
    }
}
