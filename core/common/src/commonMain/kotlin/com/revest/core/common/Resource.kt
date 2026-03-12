package com.revest.core.common

/**
 * A sealed wrapper for UI state — Success, Loading, Error.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isError get() = this is Error
}

fun <T> Result<T>.toResource(): Resource<T> = fold(
    onSuccess = { Resource.Success(it) },
    onFailure = { Resource.Error(it.message ?: "Unknown error", it) }
)
