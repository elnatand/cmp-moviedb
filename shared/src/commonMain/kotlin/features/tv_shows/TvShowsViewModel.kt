package features.tv_shows


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import features.tv_shows.data.TvShowsRepository
import features.tv_shows.model.TvShow
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTvShows()
    }

    private fun getTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            val tvShows = tvShowsRepository.getTvShowsPage()
            _uiState.update {
                it.copy(tvShows = tvShows)
            }
        }
    }

    data class UiState(
        val tvShows: List<TvShow> = emptyList()
    )
}