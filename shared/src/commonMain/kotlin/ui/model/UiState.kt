package ui.model

data class UiState<T> (

    val state: State = State.LOADING,
    val data: T? = null
)

enum class State {
    LOADING, ERROR, SUCCESS
}