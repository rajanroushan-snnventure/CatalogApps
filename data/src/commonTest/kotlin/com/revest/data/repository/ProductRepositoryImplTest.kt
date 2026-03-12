package com.revest.data.repository

import com.revest.data.remote.ProductApiService
import com.revest.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

private const val PRODUCTS_JSON = """
{
    "products": [
        {
            "id": 1,
            "title": "iPhone 15",
            "description": "Latest iPhone",
            "price": 999.0,
            "discountPercentage": 5.0,
            "rating": 4.8,
            "stock": 50,
            "brand": "Apple",
            "category": "smartphones",
            "thumbnail": "https://cdn.dummyjson.com/1/thumb.jpg",
            "images": []
        }
    ],
    "total": 194,
    "skip": 0,
    "limit": 20
}
"""

private const val PRODUCT_JSON = """
{
    "id": 1,
    "title": "iPhone 15",
    "description": "Latest iPhone",
    "price": 999.0,
    "discountPercentage": 5.0,
    "rating": 4.8,
    "stock": 50,
    "brand": "Apple",
    "category": "smartphones",
    "thumbnail": "https://cdn.dummyjson.com/1/thumb.jpg",
    "images": []
}
"""

class ProductRepositoryImplTest {

    private fun buildRepository(body: String, status: HttpStatusCode = HttpStatusCode.OK): ProductRepository {
        val engine = MockEngine { _ ->
            respond(
                content = body,
                status  = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        return ProductRepositoryImpl(ProductApiService(client))
    }

    @Test fun `getProducts maps response to domain page`() = runTest {
        val result = buildRepository(PRODUCTS_JSON).getProducts()

        assertTrue(result.isSuccess)
        with(result.getOrThrow()) {
            assertEquals(1, products.size)
            assertEquals("iPhone 15", products[0].title)
            assertEquals(194, total)
            assertTrue(hasMore)
        }
    }

    @Test fun `getProductById maps to domain product`() = runTest {
        val result = buildRepository(PRODUCT_JSON).getProductById(1)

        assertTrue(result.isSuccess)
        with(result.getOrThrow()) {
            assertEquals(1, id)
            assertEquals("Apple", brand)
            assertEquals(999.0, price, 0.01)
            // Check computed discount price
            val expected = 999.0 * (1 - 5.0 / 100)
            assertEquals(expected, discountedPrice, 0.01)
        }
    }

    @Test fun `getProducts returns failure on server error`() = runTest {
        val result = buildRepository("""{"error":"Server Error"}""", HttpStatusCode.InternalServerError)
            .getProducts()

        assertTrue(result.isFailure)
    }

    @Test fun `searchProducts returns matching products`() = runTest {
        val result = buildRepository(PRODUCTS_JSON).searchProducts("iPhone")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().products.size)
    }
}
