package com.elna.moviedb.core.model

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>

    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (value: T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (error: AppResult.Error) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(this)
    return this
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> {
    return when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Error -> this
    }
}

fun <T> AppResult<T>.getOrNull(): T? {
    return when (this) {
        is AppResult.Success -> data
        is AppResult.Error -> null
    }
}

fun <T> AppResult<T>.getOrThrow(): T {
    return when (this) {
        is AppResult.Success -> data
        is AppResult.Error -> throw throwable ?: Exception(message)
    }
}
