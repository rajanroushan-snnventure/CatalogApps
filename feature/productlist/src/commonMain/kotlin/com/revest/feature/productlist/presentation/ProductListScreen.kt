package com.revest.feature.productlist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.revest.core.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ProductListState,
    onEvent: (ProductListEvent) -> Unit,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // FIX: derive a Boolean that becomes true when the user is near the end,
    // then drive a separate LaunchedEffect off both that Boolean AND the total
    // item count — so each time new items are appended and the user is still
    // near the bottom, another load fires automatically.
    val nearBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total       = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }
    LaunchedEffect(nearBottom, state.products.size) {
        if (nearBottom && !state.isLoading && !state.isFilterLoading
            && state.canLoadMore && !state.isSearchMode
        ) {
            onEvent(ProductListEvent.LoadMore)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Revest Catalog",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.totalProducts > 0) {
                            Text(
                                text = "${state.products.size} / ${state.totalProducts} products",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    if (state.isFilterLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier
                                .size(SizeIconL)
                                .padding(end = SpacingS),
                            strokeWidth = StrokeS,
                            color       = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = { /* filter sheet */ }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor      = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { padding ->
        when {
            state.isLoading && state.products.isEmpty() && !state.isFilterLoading ->
                LoadingView(Modifier.padding(padding))

            state.error != null && state.products.isEmpty() ->
                ErrorView(
                    message = state.error,
                    onRetry = { onEvent(ProductListEvent.Refresh) },
                    modifier = Modifier.padding(padding)
                )

            else -> ProductList(
                state          = state,
                padding        = padding,
                listState      = listState,
                onEvent        = onEvent,
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
private fun ProductList(
    state: ProductListState,
    padding: PaddingValues,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onEvent: (ProductListEvent) -> Unit,
    onProductClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        LazyColumn(
            state               = listState,
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(bottom = Spacing2xl),
            verticalArrangement = Arrangement.spacedBy(SpacingL)
        ) {
            item(key = "search") {
                RevestSearchBar(
                    query         = state.searchQuery,
                    onQueryChange = { onEvent(ProductListEvent.SearchQueryChanged(it)) },
                    modifier      = Modifier.padding(horizontal = SpacingCard, vertical = SpacingXl)
                )
            }

            if (state.categories.isNotEmpty()) {
                item(key = "categories") {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = SpacingCard),
                        horizontalArrangement = Arrangement.spacedBy(SpacingMl)
                    ) {
                        item {
                            RevestFilterChip(
                                label    = "All",
                                selected = state.selectedCategory == null,
                                onClick  = { onEvent(ProductListEvent.CategorySelected(null)) }
                            )
                        }
                        items(state.categories, key = { it }) { cat ->
                            RevestFilterChip(
                                label    = cat,
                                selected = state.selectedCategory == cat,
                                onClick  = { onEvent(ProductListEvent.CategorySelected(cat)) }
                            )
                        }
                    }
                }
            }

            if (state.isSearchMode && state.searchQuery.isNotBlank()) {
                item(key = "search_header") {
                    SearchResultHeader(
                        query   = state.searchQuery,
                        count   = state.products.size,
                        onClear = { onEvent(ProductListEvent.SearchQueryChanged("")) }
                    )
                }
            }

            items(items = state.products, key = { it.id }) { product ->
                ProductCard(
                    id                 = product.id,
                    title              = product.title,
                    brand              = product.brand,
                    price              = product.price,
                    rating             = product.rating,
                    discountPercentage = product.discountPercentage,
                    thumbnail          = product.thumbnail,
                    onClick            = { onProductClick(product.id) },
                    modifier           = Modifier.padding(horizontal = SpacingCard)
                )
            }

            // Pagination spinner — shown at bottom of list while next page loads
            if (state.isLoading && state.products.isNotEmpty() && !state.isFilterLoading) {
                item(key = "loading_more") {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(SpacingCard),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(SizeIconXl),
                            strokeWidth = StrokeS
                        )
                    }
                }
            }

            // "All caught up" footer when every item is loaded
            if (!state.isLoading && !state.isFilterLoading
                && !state.canLoadMore && state.products.isNotEmpty()
                && !state.isSearchMode
            ) {
                item(key = "end_of_list") {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = SpacingCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "All ${state.totalProducts} products loaded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.products.isEmpty() && !state.isLoading && !state.isFilterLoading) {
                item(key = "empty") {
                    EmptyState(
                        message = if (state.isSearchMode)
                            "No results for \"${state.searchQuery}\""
                        else
                            "No products available"
                    )
                }
            }
        }

        // Translucent overlay spinner during filter/category switching
        AnimatedVisibility(
            visible  = state.isFilterLoading,
            enter    = fadeIn(),
            exit     = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(SizeIconXxl),
                    strokeWidth = StrokeM,
                    color       = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SearchResultHeader(query: String, count: Int, onClear: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingCard),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = "$count results for \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onClear) {
            Text("Clear", color = MaterialTheme.colorScheme.primary)
        }
    }
}
