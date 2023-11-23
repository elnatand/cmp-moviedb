package com.example.moviedb.core.model

data class UiState<T> (

    val state: State = State.LOADING,
    val data: T? = null
)

enum class State {
    LOADING, ERROR, SUCCESS
}