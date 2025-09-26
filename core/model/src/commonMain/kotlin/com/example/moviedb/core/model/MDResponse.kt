package com.example.moviedb.core.model

sealed interface MDResponse<out T> {
    data class Success<T>(val data: T) : MDResponse<T>

    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : MDResponse<Nothing>
}

inline fun <T> MDResponse<T>.onSuccess(action: (value: T) -> Unit): MDResponse<T> {
    if (this is MDResponse.Success) action(data)
    return this
}

inline fun <T> MDResponse<T>.onError(action: (error: MDResponse.Error) -> Unit): MDResponse<T> {
    if (this is MDResponse.Error) action(this)
    return this
}

inline fun <T, R> MDResponse<T>.map(transform: (T) -> R): MDResponse<R> {
    return when (this) {
        is MDResponse.Success -> MDResponse.Success(transform(data))
        is MDResponse.Error -> this
    }
}

fun <T> MDResponse<T>.getOrNull(): T? {
    return when (this) {
        is MDResponse.Success -> data
        is MDResponse.Error -> null
    }
}

fun <T> MDResponse<T>.getOrThrow(): T {
    return when (this) {
        is MDResponse.Success -> data
        is MDResponse.Error -> throw throwable ?: Exception(message)
    }
}
