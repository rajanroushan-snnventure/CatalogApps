package com.revest.feature.productdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.revest.core.ui.ErrorView
import com.revest.core.ui.LoadingView
import com.revest.core.ui.SizeHeroImage
import com.revest.core.ui.SizeIconM
import com.revest.core.ui.Spacing3xl
import com.revest.core.ui.SpacingCard
import com.revest.core.ui.SpacingL
import com.revest.core.ui.SpacingMl
import com.revest.core.ui.SpacingS
import com.revest.core.ui.SpacingXl
import com.revest.core.ui.SpacingXxl
import com.revest.core.ui.SpacingXxs
import com.revest.core.ui.SpacingXxxl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    state: ProductDetailState,
    onEvent: (ProductDetailEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = state.product?.title ?: "Product Detail",
                        maxLines   = 1,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor          = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            state.product?.let { p ->
                AddToCartBar(price = p.price, discountedPrice = p.discountedPrice)
            }
        },
        modifier = modifier
    ) { padding ->
        when {
            state.isLoading                -> LoadingView(Modifier.padding(padding))
            state.error != null            -> ErrorView(
                message  = state.error,
                onRetry  = { /* user must navigate back and re-open */ },
                modifier = Modifier.padding(padding)
            )
            state.product != null          -> ProductDetailContent(
                product  = state.product,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: com.revest.domain.model.Product,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SizeHeroImage)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model              = product.thumbnail,
                contentDescription = product.title,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
                        )
                    )
            )
        }

        Column(
            modifier            = Modifier.fillMaxWidth().padding(SpacingXxxl),
            verticalArrangement = Arrangement.spacedBy(SpacingCard)
        ) {
            Text(
                text       = product.title,
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Category chip
            Surface(
                shape = RoundedCornerShape(SpacingXxxl),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text     = product.category.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = SpacingXl, vertical = SpacingS),
                    style    = MaterialTheme.typography.labelMedium,
                    color    = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Rating + stock
            Row(
                horizontalArrangement = Arrangement.spacedBy(SpacingXl),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                StarRating(rating = product.rating)
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text  = "${product.stock} in stock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.stock > 20) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Brand
            product.brand?.let { LabelValueRow("Brand", it) }

            // Discount badge
            if (product.discountPercentage > 0) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingXl)
                ) {
                    Surface(
                        shape = RoundedCornerShape(SpacingMl),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text       = "-${product.discountPercentage.toInt()}% OFF",
                            modifier   = Modifier.padding(horizontal = SpacingL, vertical = SpacingS),
                            style      = MaterialTheme.typography.labelMedium,
                            color      = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = "Was $${"%.2f".format(product.originalPrice)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text  = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(SpacingMl))
        }
    }
}

@Composable
private fun StarRating(rating: Double) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingXxs)
    ) {
        repeat(5) { i ->
            Icon(
                imageVector        = Icons.Filled.Star,
                contentDescription = null,
                modifier           = Modifier.size(SpacingCard),
                tint               = if (i < rating.toInt()) MaterialTheme.colorScheme.tertiary
                                     else MaterialTheme.colorScheme.outlineVariant
            )
        }
        Spacer(Modifier.width(SpacingS))
        Text(
            text       = "%.1f".format(rating),
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AddToCartBar(price: Double, discountedPrice: Double) {
    Surface(shadowElevation = SpacingMl, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = SpacingXxxl, vertical = SpacingXl),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Price",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text       = "$${"%.2f".format(discountedPrice)}",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick       = { },
                colors        = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = Spacing3xl, vertical = SpacingXxl)
            ) {
                Icon(Icons.Filled.ShoppingCart, null, Modifier.size(SizeIconM))
                Spacer(Modifier.width(SpacingMl))
                Text("Add to Cart", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
