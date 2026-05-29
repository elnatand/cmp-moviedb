package com.elna.moviedb.core.model

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>

    data class Error(
        val message: String? = null,
        val code: Int? = null,
        val throwable: Throwable? = null,
        val type: DataError = DataError.UNKNOWN,
    ) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> {
    return when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Error -> this
    }
}
