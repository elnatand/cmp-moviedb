# SOLID and Clean Architecture Violations Review
## CMP MovieDB Project - Comprehensive Analysis

**Date:** 2025-10-18
**Reviewer:** Claude Code
**Scope:** Complete codebase analysis for SOLID principles and Clean Architecture violations

---

## Executive Summary

This document provides an in-depth analysis of SOLID principles and Clean Architecture patterns in the CMP MovieDB Kotlin Multiplatform project. The codebase demonstrates **strong architectural foundations** with excellent use of design patterns (Strategy, Repository, MVI) and proper dependency injection. However, several violations and areas for improvement have been identified.

### Overall Assessment

**Strengths:**
- ✅ Excellent Open/Closed Principle implementation (MovieCategory, SearchFilter enums)
- ✅ Strong Dependency Inversion with interface-based design
- ✅ Good separation of concerns between layers
- ✅ Proper use of MVI/UDF pattern for predictable state management
- ✅ Type-safe error handling with sealed classes

**Areas for Improvement:**
- ❌ Missing Use Case/Domain layer leads to business logic in repositories and ViewModels
- ❌ Single Responsibility violations in repositories and data sources
- ❌ Interface Segregation violations in large interfaces
- ❌ Data transformation happening in wrong layers
- ❌ Database transaction atomicity issues
- ❌ Code duplication in search implementations

---

## 1. SOLID Principles Analysis

### 1.1 Single Responsibility Principle (SRP) Violations

#### 🔴 CRITICAL: MoviesRepositoryImpl Has Multiple Responsibilities

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt`

**Violation:**
The `MoviesRepositoryImpl` class handles too many responsibilities:

1. **Caching Strategy Logic** - Decides when to use cache vs network
2. **Network Coordination** - Manages parallel API calls (details, videos, credits)
3. **Pagination Management** - Tracks current page and total pages
4. **Data Transformation** - Converts DTOs to domain models, applies URL transformations
5. **Error Handling Strategy** - Decides graceful degradation for videos/cast
6. **Database Operations** - Orchestrates multiple DB writes

```kotlin
// Lines 133-234: Single method with 100+ lines doing everything
override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> = coroutineScope {
    // 1. Cache checking logic
    val cachedMovieDetails = moviesLocalDataSource.getMoviesDetails(movieId)
    if (cachedMovieDetails != null) { /* ... */ }

    // 2. Parallel network calls coordination
    val detailsDeferred = async { /* ... */ }
    val videosDeferred = async { /* ... */ }
    val creditsDeferred = async { /* ... */ }

    // 3. Error handling strategy (graceful degradation)
    val trailers = when (videosResult) { /* ... */ }

    // 4. Data transformation (filtering, sorting, mapping)
    val trailers = videosResult.data.results
        .filter { it.type == "Trailer" || it.type == "Teaser" }
        .sortedWith(compareByDescending<RemoteVideo> { it.official })

    // 5. Database write orchestration
    moviesLocalDataSource.insertMovieDetails(detailsEntity)
    moviesLocalDataSource.deleteVideosForMovie(movieId)
    moviesLocalDataSource.insertVideos(videoEntities)
    moviesLocalDataSource.replaceCastForMovie(movieId, castEntities)

    // 6. URL transformation
    posterPath = detailsEntity.posterPath.toFullImageUrl()
}
```

**Impact:**
- Difficult to test individual responsibilities
- High cyclomatic complexity (multiple decision points)
- Changes to caching strategy require modifying repository
- Changes to error handling affect caching logic
- Hard to reuse caching or network coordination logic elsewhere

**Recommendation:**
Extract responsibilities into separate classes:

```kotlin
// Recommended structure
interface CachingStrategy {
    suspend fun <T> execute(
        cacheKey: String,
        fetchFromCache: suspend () -> T?,
        fetchFromNetwork: suspend () -> T,
        saveToCache: suspend (T) -> Unit
    ): AppResult<T>
}

interface MovieDetailsAggregator {
    suspend fun fetchMovieDetails(movieId: Int, language: String): MovieDetailsAggregate
}

class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
    private val cachingStrategy: CachingStrategy,
    private val detailsAggregator: MovieDetailsAggregator,
    private val urlTransformer: ImageUrlTransformer
) : MoviesRepository {
    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        return cachingStrategy.execute(
            cacheKey = "movie_$movieId",
            fetchFromCache = { localDataSource.getMovieDetails(movieId) },
            fetchFromNetwork = { detailsAggregator.fetchMovieDetails(movieId, language) },
            saveToCache = { localDataSource.saveMovieDetails(it) }
        ).map { urlTransformer.transform(it) }
    }
}
```

---

#### 🔴 CRITICAL: MoviesLocalDataSource Violates SRP

**Location:** `core/database/src/commonMain/kotlin/com/elna/moviedb/core/database/MoviesLocalDataSource.kt`

**Violation:**
Single interface mixing four different entity types:

```kotlin
interface MoviesLocalDataSource {
    // Movie list operations
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    suspend fun clearAllMovies()

    // Movie details operations
    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity?
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)

    // Video operations
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    suspend fun insertVideos(videos: List<VideoEntity>)
    suspend fun deleteVideosForMovie(movieId: Int)

    // Cast operations
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)
    suspend fun deleteCastForMovie(movieId: Int)
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}
```

**Impact:**
- Clients that only need movie list data must depend on entire interface
- Changes to video operations affect all clients
- Violates Interface Segregation Principle (ISP) as well
- Testing becomes complex due to large surface area

**Recommendation:**
Split into focused interfaces:

```kotlin
interface MoviesListDataSource {
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    suspend fun clearAllMovies()
}

interface MovieDetailsDataSource {
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
}

interface MovieVideosDataSource {
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    suspend fun insertVideos(videos: List<VideoEntity>)
    suspend fun deleteVideosForMovie(movieId: Int)
}

interface MovieCastDataSource {
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}

// Composite for clients that need multiple sources
interface MoviesDataSource :
    MoviesListDataSource,
    MovieDetailsDataSource,
    MovieVideosDataSource,
    MovieCastDataSource
```

---

#### 🟡 MODERATE: SearchRepositoryImpl Code Duplication

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/search/SearchRepositoryImpl.kt`

**Violation:**
Repeated pattern for each search filter with only minor variations:

```kotlin
// Lines 44-72: ALL filter logic
SearchFilter.ALL -> {
    val remoteResult = searchRemoteDataSource.searchMulti(query, page, language)
    when (remoteResult) {
        is AppResult.Success -> {
            val searchItems = remoteResult.data.results.mapNotNull { /* mapping */ }
            AppResult.Success(searchItems)
        }
        is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
    }
}

// Lines 75-92: MOVIES filter - nearly identical pattern
SearchFilter.MOVIES -> {
    val remoteResult = searchRemoteDataSource.searchMovies(query, page, language)
    when (remoteResult) {
        is AppResult.Success -> {
            val movieItems = remoteResult.data.results.map { /* mapping */ }
            AppResult.Success(movieItems)
        }
        is AppResult.Error -> AppResult.Error(remoteResult.message, throwable = remoteResult.throwable)
    }
}
// ... same pattern repeated for TV_SHOWS and PEOPLE
```

**Impact:**
- Duplicated error handling logic (4 times)
- Duplicated result mapping pattern
- Changes to error handling must be applied 4 times
- Increased maintenance burden

**Recommendation:**
Extract common pattern:

```kotlin
class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val languageProvider: LanguageProvider
) : SearchRepository {

    override fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val language = languageProvider.getCurrentLanguage()

        // Delegate to filter-specific fetcher, then apply common transformation
        val result = executeSearch(filter, query, page, language)
            .mapSuccess { items -> items.map { it.withFullImageUrls() } }

        emit(result)
    }

    private suspend fun executeSearch(
        filter: SearchFilter,
        query: String,
        page: Int,
        language: String
    ): AppResult<List<SearchResultItem>> {
        return when (filter) {
            SearchFilter.ALL -> searchRemoteDataSource.searchMulti(query, page, language)
                .mapSuccess { it.results.mapNotNull { item -> item.toSearchResult() } }
            SearchFilter.MOVIES -> searchRemoteDataSource.searchMovies(query, page, language)
                .mapSuccess { it.results.map { item -> item.toSearchResult() } }
            SearchFilter.TV_SHOWS -> searchRemoteDataSource.searchTvShows(query, page, language)
                .mapSuccess { it.results.map { item -> item.toSearchResult() } }
            SearchFilter.PEOPLE -> searchRemoteDataSource.searchPeople(query, page, language)
                .mapSuccess { it.results.map { item -> item.toSearchResult() } }
        }
    }

    // Extension function for AppResult mapping
    private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> {
        return when (this) {
            is AppResult.Success -> AppResult.Success(transform(data))
            is AppResult.Error -> this
        }
    }
}
```

---

#### 🟡 MODERATE: MoviesViewModel Handles Too Many Concerns

**Location:** `features/movies/src/commonMain/kotlin/com/elna/moviedb/feature/movies/ui/movies/MoviesViewModel.kt`

**Violation:**
ViewModel manages:
1. Multiple category states (map-based state management)
2. Per-category loading states
3. Pagination logic and loading prevention
4. Error handling strategy (initial error vs pagination error)
5. Retry coordination across all categories
6. Flow observation for all categories

```kotlin
// Lines 72-89: Observes all categories
private fun observeMovies() {
    MovieCategory.entries.forEach { category ->
        viewModelScope.launch {
            moviesRepository.observeMovies(category).collect { movies ->
                _uiState.update { /* complex state update logic */ }
            }
        }
    }
}

// Lines 104-147: loadNextPage with complex logic
private fun loadNextPage(category: MovieCategory) {
    // 1. Loading state check
    if (_uiState.value.isLoading(category)) return

    // 2. Update loading state
    _uiState.update { /* ... */ }

    // 3. Error handling strategy decision
    val currentMovies = _uiState.value.getMovies(category)
    if (currentMovies.isNotEmpty()) {
        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
    } else {
        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
    }
}

// Lines 155-174: Retry coordination
private fun retry() {
    // Loads all categories in parallel
    val results = MovieCategory.entries.map { category ->
        async { moviesRepository.loadMoviesNextPage(category) }
    }.awaitAll()

    // Evaluates combined results
    val hasSuccess = results.any { it is AppResult.Success }
    // ...
}
```

**Impact:**
- ViewModel becomes difficult to test due to multiple responsibilities
- Business logic (error handling strategy) in presentation layer
- Hard to reuse pagination logic in other features

**Recommendation:**
Extract pagination and error handling logic into use cases:

```kotlin
// Domain layer
class LoadMoviesNextPageUseCase(
    private val repository: MoviesRepository,
    private val errorStrategy: PaginationErrorStrategy
) {
    suspend operator fun invoke(category: MovieCategory): PaginationResult {
        return when (val result = repository.loadMoviesNextPage(category)) {
            is AppResult.Success -> PaginationResult.Success
            is AppResult.Error -> errorStrategy.handleError(result, category)
        }
    }
}

sealed class PaginationResult {
    object Success : PaginationResult()
    data class ErrorWithSnackbar(val message: String) : PaginationResult()
    data class ErrorWithScreen(val message: String) : PaginationResult()
}

// Simplified ViewModel
class MoviesViewModel(
    private val loadNextPageUseCase: LoadMoviesNextPageUseCase,
    observeMoviesUseCase: ObserveMoviesUseCase
) : ViewModel() {

    val uiState: StateFlow<MoviesUiState> = observeMoviesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MoviesUiState())

    fun onEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.LoadNextPage -> loadNextPage(event.category)
            MoviesEvent.Retry -> retry()
        }
    }

    private fun loadNextPage(category: MovieCategory) {
        viewModelScope.launch {
            when (val result = loadNextPageUseCase(category)) {
                is PaginationResult.ErrorWithSnackbar -> _uiAction.send(ShowSnackbar(result.message))
                is PaginationResult.ErrorWithScreen -> _uiState.update { it.copy(state = ERROR) }
                PaginationResult.Success -> { /* handled by flow */ }
            }
        }
    }
}
```

---

### 1.2 Open/Closed Principle (OCP) Analysis

#### ✅ EXCELLENT: MovieCategory and SearchFilter Implementation

**Location:** `core/model/src/commonMain/kotlin/com/elna/moviedb/core/model/`

**Strength:**
Excellent use of enums with associated data to support extension without modification:

```kotlin
enum class MovieCategory(val apiPath: String) {
    POPULAR("movie/popular"),
    TOP_RATED("movie/top_rated"),
    NOW_PLAYING("movie/now_playing")
}

enum class SearchFilter(val apiPath: String) {
    ALL("search/multi"),
    MOVIES("search/movie"),
    TV_SHOWS("search/tv"),
    PEOPLE("search/person")
}
```

This design allows:
- Adding new categories without modifying repository code
- ViewModels automatically handle new categories via `entries.forEach`
- Search functionality extensible through new enum values

**Example of OCP adherence in MoviesViewModel:**
```kotlin
// Lines 72-89: No hardcoded categories!
private fun observeMovies() {
    MovieCategory.entries.forEach { category ->  // ✅ Open for extension
        viewModelScope.launch {
            moviesRepository.observeMovies(category).collect { movies ->
                // Generic handling for any category
            }
        }
    }
}
```

---

#### 🟡 MINOR: SearchViewModel Strategy Mapping Requires Modification

**Location:** `features/search/src/commonMain/kotlin/com/elna/moviedb/feature/search/ui/SearchViewModel.kt:45-52`

**Violation:**
Adding a new search filter requires modifying `getStrategyForFilter()`:

```kotlin
private fun getStrategyForFilter(filter: SearchFilter): SearchStrategy {
    return when (filter) {
        SearchFilter.ALL -> AllSearchStrategy(searchRepository)
        SearchFilter.MOVIES -> MovieSearchStrategy(searchRepository)
        SearchFilter.TV_SHOWS -> TvShowSearchStrategy(searchRepository)
        SearchFilter.PEOPLE -> PeopleSearchStrategy(searchRepository)
        // ❌ Adding new filter requires modification here
    }
}
```

**Impact:**
- Minor violation since it's isolated to one method
- New search types require code changes in ViewModel

**Recommendation:**
Use a factory pattern or strategy registry:

```kotlin
// Strategy factory with registration
class SearchStrategyFactory(private val repository: SearchRepository) {
    private val strategies = mutableMapOf<SearchFilter, SearchStrategy>()

    init {
        register(SearchFilter.ALL, AllSearchStrategy(repository))
        register(SearchFilter.MOVIES, MovieSearchStrategy(repository))
        register(SearchFilter.TV_SHOWS, TvShowSearchStrategy(repository))
        register(SearchFilter.PEOPLE, PeopleSearchStrategy(repository))
    }

    fun register(filter: SearchFilter, strategy: SearchStrategy) {
        strategies[filter] = strategy
    }

    fun getStrategy(filter: SearchFilter): SearchStrategy {
        return strategies[filter]
            ?: throw IllegalArgumentException("No strategy for filter: $filter")
    }
}

// ViewModel becomes truly open/closed
class SearchViewModel(
    private val searchStrategyFactory: SearchStrategyFactory
) : ViewModel() {
    private fun performSearch(query: String, filter: SearchFilter, page: Int) {
        val strategy = searchStrategyFactory.getStrategy(filter)  // ✅ No modification needed
        // ...
    }
}
```

---

#### 🟢 MINOR: SearchStrategy Pattern May Be Over-Engineered

**Location:** `features/search/src/commonMain/kotlin/com/elna/moviedb/feature/search/strategy/SearchStrategy.kt`

**Observation:**
Strategy implementations are very thin wrappers:

```kotlin
class AllSearchStrategy(private val repository: SearchRepository) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.ALL, query, page)  // Just delegates
    }
}

class MovieSearchStrategy(private val repository: SearchRepository) : SearchStrategy {
    override fun search(query: String, page: Int): Flow<AppResult<List<SearchResultItem>>> {
        return repository.search(SearchFilter.MOVIES, query, page)  // Just delegates
    }
}
// ... all strategies are identical except for the enum value
```

**Analysis:**
- Each strategy is a 4-line wrapper around repository call
- No additional logic or behavior differentiation
- Strategy pattern adds complexity without clear benefit
- Repository already handles filtering through `SearchFilter` enum

**Impact:**
- Increased code volume without added value
- More classes to maintain
- Testing overhead (need to test each strategy wrapper)

**Recommendation:**
Consider removing the Strategy pattern since the repository already provides the abstraction:

```kotlin
// Simplified - repository is already the strategy
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private fun performSearch(query: String, filter: SearchFilter, page: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Repository already handles filter-specific logic
            val result = searchRepository.search(filter, query, page).first()

            _uiState.update { currentState ->
                when (result) {
                    is AppResult.Success -> currentState.copy(
                        searchResults = result.data,
                        isLoading = false
                    )
                    is AppResult.Error -> currentState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
```

This is simpler and the repository's `when(filter)` statement provides the same extensibility.

---

### 1.3 Liskov Substitution Principle (LSP) Analysis

#### ✅ NO VIOLATIONS FOUND

**Analysis:**
All implementations properly substitute their interfaces:

1. **Repository Implementations:**
   - `MoviesRepositoryImpl`, `SearchRepositoryImpl`, `PersonRepositoryImpl` all properly implement their interfaces
   - No unexpected exceptions or behavioral changes
   - Contract honored: return `AppResult<T>` as specified

2. **Data Source Implementations:**
   - `MoviesLocalDataSourceImpl` correctly implements `MoviesLocalDataSource`
   - All DAOs properly implement Room's `@Dao` contract

3. **ViewModel Hierarchy:**
   - All ViewModels extend `ViewModel` without violating its contract
   - State management follows expected patterns

4. **Sealed Classes:**
   - `AppResult.Success` and `AppResult.Error` properly implement sealed interface
   - `SearchResultItem` subtypes (MovieItem, TvShowItem, PersonItem) maintain contract

**Recommendation:**
Continue maintaining strong interface contracts and comprehensive tests to ensure LSP compliance.

---

### 1.4 Interface Segregation Principle (ISP) Violations

#### 🔴 CRITICAL: MoviesLocalDataSource Interface Too Large

**Location:** `core/database/src/commonMain/kotlin/com/elna/moviedb/core/database/MoviesLocalDataSource.kt:13-26`

**Violation:**
Clients forced to depend on operations they don't use:

```kotlin
interface MoviesLocalDataSource {
    // Movies list operations (3 methods)
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>
    suspend fun insertMoviesPage(movies: List<MovieEntity>)
    suspend fun clearAllMovies()

    // Movie details (2 methods)
    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity?
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)

    // Videos (3 methods)
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    suspend fun insertVideos(videos: List<VideoEntity>)
    suspend fun deleteVideosForMovie(movieId: Int)

    // Cast members (4 methods)
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)
    suspend fun deleteCastForMovie(movieId: Int)
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}
```

**Impact:**
- Repository that only needs movie list data must depend on 12 methods
- Mocking for tests requires implementing all 12 methods
- Changes to cast operations force recompilation of all clients
- Violates "clients should not be forced to depend on methods they do not use"

**Example of Impact:**
If you had a `PopularMoviesWidget` that only shows popular movies:
```kotlin
class PopularMoviesWidget(
    private val dataSource: MoviesLocalDataSource  // ❌ Depends on 12 methods
) {
    fun getPopularMovies(): Flow<List<MovieEntity>> {
        return dataSource.getMoviesByCategoryAsFlow("POPULAR")  // Only uses 1 method!
    }
}
```

**Recommendation:**
Already covered in SRP section - split into smaller interfaces.

---

#### 🔴 MODERATE: MovieDetailsDao Combines Multiple Concerns

**Location:** `core/database/src/commonMain/kotlin/com/elna/moviedb/core/database/MovieDetailsDao.kt`

**Violation:**
DAO interface mixes three entity types:

```kotlin
@Dao
interface MovieDetailsDao {
    // Movie details operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(item: MovieDetailsEntity)
    @Query("SELECT * FROM MovieDetailsEntity WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    // Video operations
    @Query("SELECT * FROM videos WHERE movie_id = :movieId")
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    // Cast operations
    @Query("SELECT * FROM cast_members WHERE movie_id = :movieId")
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)
    @Transaction
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>)
}
```

**Impact:**
- Forces clients to depend on operations they don't need
- Violates single responsibility
- Changes to video schema affect cast operation clients

**Recommendation:**
Split into focused DAOs:

```kotlin
@Dao
interface MovieDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(item: MovieDetailsEntity)

    @Query("SELECT * FROM MovieDetailsEntity WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    @Query("DELETE FROM MovieDetailsEntity")
    suspend fun clearAllMovieDetails()
}

@Dao
interface MovieVideosDao {
    @Query("SELECT * FROM videos WHERE movie_id = :movieId ORDER BY official DESC")
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("DELETE FROM videos WHERE movie_id = :movieId")
    suspend fun deleteVideosForMovie(movieId: Int)
}

@Dao
interface MovieCastDao {
    @Query("SELECT * FROM cast_members WHERE movie_id = :movieId ORDER BY `order` ASC")
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)

    @Query("DELETE FROM cast_members WHERE movie_id = :movieId")
    suspend fun deleteCastForMovie(movieId: Int)

    @Transaction
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>) {
        deleteCastForMovie(movieId)
        if (cast.isNotEmpty()) insertCastMembers(cast)
    }
}
```

---

#### ✅ GOOD: ProfileViewModel Interface Segregation

**Location:** `features/profile/src/commonMain/kotlin/com/elna/moviedb/feature/profile/ui/ProfileViewModel.kt:28`

**Strength:**
Excellent example of ISP - depends only on what it needs:

```kotlin
class ProfileViewModel(
    private val appSettingsPreferences: AppSettingsPreferences  // ✅ Only app settings
) : ViewModel() {
    // Does NOT depend on PaginationPreferences which it doesn't need
}
```

The codebase correctly separates:
- `AppSettingsPreferences` - language and theme settings
- `PaginationPreferences` - pagination state

This allows ProfileViewModel to depend only on settings it needs, not the full preferences interface.

---

### 1.5 Dependency Inversion Principle (DIP) Analysis

#### ✅ EXCELLENT: Repository Pattern Implementation

**Strengths:**
All repositories depend on abstractions (interfaces), not concrete implementations:

```kotlin
// High-level module (Repository) depends on abstraction
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,  // ✅ Interface
    private val moviesLocalDataSource: MoviesLocalDataSource,     // ✅ Interface
    private val paginationPreferences: PaginationPreferences,     // ✅ Interface
    private val languageProvider: LanguageProvider,               // ✅ Class, but could be interface
) : MoviesRepository  // ✅ Implements interface

// ViewModels depend on abstractions
class MoviesViewModel(
    private val moviesRepository: MoviesRepository  // ✅ Interface, not implementation
) : ViewModel()
```

**Koin DI Configuration:**
```kotlin
// Binds interfaces to implementations
single<MoviesRepository> {
    MoviesRepositoryImpl(
        moviesRemoteDataSource = get(),
        moviesLocalDataSource = get(),
        paginationPreferences = get(),
        languageProvider = get(),
    )
}
```

---

#### 🟡 MINOR: LanguageProvider Could Be An Interface

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/util/LanguageProvider.kt`

**Observation:**
`LanguageProvider` is a concrete class, not an interface:

```kotlin
// Current implementation
class LanguageProvider(
    private val appSettingsPreferences: AppSettingsPreferences
) {
    suspend fun getCurrentLanguage(): String {
        return appSettingsPreferences.getAppLanguageCode().first()
    }

    fun getFormattedLanguage(languageCode: String): String {
        return when (languageCode) {
            "en" -> "en-US"
            "es" -> "es-ES"
            // ...
        }
    }
}
```

**Impact:**
- Minor violation - limits testability
- Repositories depend on concrete class instead of abstraction
- Cannot easily mock or swap implementation

**Recommendation:**
```kotlin
interface LanguageProvider {
    suspend fun getCurrentLanguage(): String
    fun getFormattedLanguage(languageCode: String): String
}

class LanguageProviderImpl(
    private val appSettingsPreferences: AppSettingsPreferences
) : LanguageProvider {
    override suspend fun getCurrentLanguage(): String {
        return appSettingsPreferences.getAppLanguageCode().first()
    }

    override fun getFormattedLanguage(languageCode: String): String {
        return when (languageCode) {
            "en" -> "en-US"
            "es" -> "es-ES"
            // ...
        }
    }
}

// DI configuration
single<LanguageProvider> { LanguageProviderImpl(get()) }
```

---

#### ✅ GOOD: LanguageChangeCoordinator Depends on Abstraction

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/LanguageChangeCoordinator.kt:19-23`

**Strength:**
Coordinator depends only on `AppSettingsPreferences` interface, not full preferences:

```kotlin
class LanguageChangeCoordinator(
    appSettingsPreferences: AppSettingsPreferences,  // ✅ Interface, following ISP
    scope: CoroutineScope,
    onLanguageChange: suspend () -> Unit  // ✅ Callback abstraction
) {
    init {
        scope.launch {
            appSettingsPreferences.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1)
                .collect { onLanguageChange() }
        }
    }
}
```

Excellent adherence to both DIP and ISP!

---

## 2. Clean Architecture Violations

### 2.1 Missing Domain/Use Case Layer

#### 🔴 CRITICAL: No Explicit Use Case Layer

**Violation:**
The project lacks a dedicated domain/use case layer. Business logic is scattered between:

1. **Repositories** (Data Layer) - Handle business rules like:
   - Offline-first caching strategy
   - Parallel API call coordination
   - Graceful degradation for optional data
   - Data filtering and sorting

2. **ViewModels** (Presentation Layer) - Handle business rules like:
   - Pagination error handling strategy (snackbar vs error screen)
   - Retry coordination across categories
   - Loading state management

**Example: Business Logic in Repository**

`MoviesRepositoryImpl.kt:154-193` - Business rules about what constitutes a "trailer":

```kotlin
// ❌ This is business logic, not data access logic
val trailers = when (videosResult) {
    is AppResult.Success -> {
        videosResult.data.results
            .filter { it.type == "Trailer" || it.type == "Teaser" }  // Business rule
            .sortedWith(
                compareByDescending<RemoteVideo> { it.official }     // Business rule
                    .thenByDescending { it.publishedAt }             // Business rule
            )
            .map { it.toDomain() }
    }
    is AppResult.Error -> emptyList()  // Business rule: graceful degradation
}
```

**Example: Business Logic in ViewModel**

`MoviesViewModel.kt:126-133` - Business rule for error handling:

```kotlin
// ❌ This is business logic about UX strategy, not presentation logic
val currentMovies = _uiState.value.getMovies(category)
if (currentMovies.isNotEmpty()) {
    _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))  // Business rule
} else {
    _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }      // Business rule
}
```

**Impact:**
- Business rules cannot be reused across features
- Difficult to test business logic in isolation
- ViewModels become fat with business logic
- Repositories do more than data access
- Violates Clean Architecture's dependency rule

**Recommendation:**
Introduce a **Use Case** layer:

```kotlin
// domain/usecase/movies/GetMovieDetailsUseCase.kt
class GetMovieDetailsUseCase(
    private val repository: MoviesRepository,
    private val trailerFilter: TrailerFilterStrategy
) {
    suspend operator fun invoke(movieId: Int): AppResult<MovieDetails> {
        return repository.getMovieDetails(movieId)
            .map { details ->
                details.copy(
                    trailers = trailerFilter.filterAndSort(details.trailers)
                )
            }
    }
}

// domain/usecase/movies/TrailerFilterStrategy.kt
interface TrailerFilterStrategy {
    fun filterAndSort(videos: List<Video>): List<Video>
}

class OfficialTrailersFirstStrategy : TrailerFilterStrategy {
    override fun filterAndSort(videos: List<Video>): List<Video> {
        return videos
            .filter { it.type == "Trailer" || it.type == "Teaser" }
            .sortedWith(
                compareByDescending<Video> { it.isOfficial }
                    .thenByDescending { it.publishedAt }
            )
    }
}

// domain/usecase/movies/LoadMoviesNextPageUseCase.kt
class LoadMoviesNextPageUseCase(
    private val repository: MoviesRepository,
    private val errorStrategy: PaginationErrorStrategy
) {
    suspend operator fun invoke(
        category: MovieCategory,
        currentMovies: List<Movie>
    ): LoadNextPageResult {
        return when (val result = repository.loadMoviesNextPage(category)) {
            is AppResult.Success -> LoadNextPageResult.Success
            is AppResult.Error -> errorStrategy.handlePaginationError(
                error = result,
                hasExistingData = currentMovies.isNotEmpty()
            )
        }
    }
}

sealed class LoadNextPageResult {
    object Success : LoadNextPageResult()
    data class ErrorShowSnackbar(val message: String) : LoadNextPageResult()
    data class ErrorShowScreen(val message: String) : LoadNextPageResult()
}

// domain/strategy/PaginationErrorStrategy.kt
interface PaginationErrorStrategy {
    fun handlePaginationError(error: AppResult.Error, hasExistingData: Boolean): LoadNextPageResult
}

class DefaultPaginationErrorStrategy : PaginationErrorStrategy {
    override fun handlePaginationError(
        error: AppResult.Error,
        hasExistingData: Boolean
    ): LoadNextPageResult {
        return if (hasExistingData) {
            LoadNextPageResult.ErrorShowSnackbar(error.message)
        } else {
            LoadNextPageResult.ErrorShowScreen(error.message)
        }
    }
}

// Simplified Repository (only data access)
class MoviesRepositoryImpl(...) : MoviesRepository {
    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        // ✅ Only handles data fetching and caching
        // ❌ No business rules about filtering/sorting
        return cachingStrategy.execute(
            fetchFromCache = { localDataSource.getMovieDetails(movieId) },
            fetchFromNetwork = {
                remoteDataSource.getMovieDetails(movieId)
                    .zipWith(remoteDataSource.getMovieVideos(movieId))
                    .zipWith(remoteDataSource.getMovieCredits(movieId))
            },
            saveToCache = { localDataSource.saveMovieDetails(it) }
        )
    }
}

// Simplified ViewModel (only presentation logic)
class MoviesViewModel(
    private val loadNextPageUseCase: LoadMoviesNextPageUseCase,
    private val observeMoviesUseCase: ObserveMoviesUseCase
) : ViewModel() {

    private fun loadNextPage(category: MovieCategory) {
        viewModelScope.launch {
            val currentMovies = _uiState.value.getMovies(category)

            when (val result = loadNextPageUseCase(category, currentMovies)) {
                LoadNextPageResult.Success -> { /* handled by flow */ }
                is LoadNextPageResult.ErrorShowSnackbar ->
                    _uiAction.send(ShowSnackbar(result.message))
                is LoadNextPageResult.ErrorShowScreen ->
                    _uiState.update { it.copy(state = ERROR) }
            }
        }
    }
}
```

**Benefits:**
- ✅ Business logic isolated and testable
- ✅ Reusable across features
- ✅ ViewModels focus on presentation
- ✅ Repositories focus on data access
- ✅ Follows Clean Architecture dependency rule

---

### 2.2 Layer Dependency Violations

#### 🟡 MODERATE: Data Transformation in Repository Layer

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt`

**Violation:**
Image URL transformation happens in the repository (data layer):

```kotlin
// Lines 143, 227-228
val cast = cachedCast.sortedBy { it.order }
    .map { it.toDomain().copy(profilePath = it.profilePath.toFullImageUrl()) }  // ❌ UI concern

val movieDetails = cachedMovieDetails.toDomain().copy(
    posterPath = cachedMovieDetails.posterPath.toFullImageUrl(),     // ❌ UI concern
    backdropPath = cachedMovieDetails.backdropPath.toFullImageUrl(), // ❌ UI concern
    trailers = trailers,
    cast = cast
)
```

**Analysis:**
Image URL transformation (adding base URL and size) is a presentation concern:
- Base URL: `https://image.tmdb.org/t/p/w500/`
- Different screen sizes might need different image sizes (w200, w500, original)
- Violates Clean Architecture: data layer should not know about UI requirements

**Impact:**
- Cannot easily change image sizes per screen
- Data layer coupled to UI requirements
- Domain models polluted with full URLs

**Recommendation:**
Move URL transformation to presentation layer:

```kotlin
// Domain model keeps relative paths
data class MovieDetails(
    val posterPath: String?,      // ✅ "/path/to/poster.jpg" (relative)
    val backdropPath: String?,
    // ...
)

// Presentation layer extension
fun String?.toImageUrl(size: ImageSize = ImageSize.W500): String {
    if (this == null) return ""
    return "https://image.tmdb.org/t/p/${size.path}$this"
}

enum class ImageSize(val path: String) {
    W200("w200"),
    W500("w500"),
    ORIGINAL("original")
}

// Usage in Composable
@Composable
fun MoviePoster(movie: MovieDetails) {
    AsyncImage(
        model = movie.posterPath.toImageUrl(ImageSize.W500),  // ✅ UI decides size
        contentDescription = movie.title
    )
}

// Or use a presentation model
data class MovieDetailsUiModel(
    val posterUrl: String,
    val backdropUrl: String,
    // ...
) {
    companion object {
        fun from(domain: MovieDetails, imageSize: ImageSize = ImageSize.W500): MovieDetailsUiModel {
            return MovieDetailsUiModel(
                posterUrl = domain.posterPath.toImageUrl(imageSize),
                backdropUrl = domain.backdropPath.toImageUrl(imageSize)
            )
        }
    }
}
```

---

#### 🟡 MODERATE: DTO-to-Domain Mapping in Repository

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt:100`

**Violation:**
Repository performs DTO-to-entity mapping:

```kotlin
// Line 100
val entities = result.data.results.map {
    it.asEntity().copy(category = category.name)  // ❌ Mapping in repository
}
```

**Analysis:**
Mapping between DTOs and entities is a data layer concern, but should be in the data source, not repository:

**Current flow:**
```
Remote Data Source → Repository → Entity
                    (mapping happens here)
```

**Clean Architecture flow:**
```
Remote Data Source → Entity → Repository → Domain
(mapping here)              (domain mapping here)
```

**Recommendation:**
Move DTO-to-entity mapping to data source:

```kotlin
// MoviesRemoteDataSource - should return entities or domain models
suspend fun fetchMoviesPage(
    apiPath: String,
    page: Int,
    language: String,
    category: String  // Pass category down
): AppResult<MoviesPage> {  // Return domain model or entity list
    return withContext(appDispatchers.io) {
        safeApiCall {
            val response = httpClient.get("${TMDB_BASE_URL}$apiPath") {
                url {
                    parameters.append("api_key", TMDB_API_KEY)
                    parameters.append("page", page.toString())
                    parameters.append("language", language)
                }
            }.body<RemoteMoviesPage>()

            // ✅ Mapping happens in data source
            MoviesPage(
                results = response.results.map { it.toEntity(category) },
                totalPages = response.totalPages,
                currentPage = page
            )
        }
    }
}

// Repository just orchestrates
override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
    val language = languageProvider.getCurrentLanguage()
    val nextPage = paginationPreferences.getNextPage(category.name)

    return when (val result = remoteDataSource.fetchMoviesPage(
        apiPath = category.apiPath,
        page = nextPage,
        language = language,
        category = category.name
    )) {
        is AppResult.Success -> {
            localDataSource.insertMoviesPage(result.data.results)  // ✅ Already entities
            paginationPreferences.savePaginationState(
                category.name,
                PaginationState(nextPage, result.data.totalPages)
            )
            AppResult.Success(Unit)
        }
        is AppResult.Error -> result
    }
}
```

---

### 2.3 Database Transaction Atomicity Issues

#### 🔴 CRITICAL: Movie Details Insertion Not Atomic

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt:194-217`

**Violation:**
Multiple database operations without transaction wrapper:

```kotlin
// Lines 194-217: Not wrapped in a transaction
val detailsEntity = details.asEntity()
moviesLocalDataSource.insertMovieDetails(detailsEntity)  // Operation 1

// Replace existing trailers atomically
moviesLocalDataSource.deleteVideosForMovie(movieId)       // Operation 2
if (trailers.isNotEmpty()) {
    val videoEntities = trailers.map { it.asEntity(movieId = movieId) }
    moviesLocalDataSource.insertVideos(videoEntities)     // Operation 3
}

val castEntities = remoteCast.map { /* ... */ }
moviesLocalDataSource.replaceCastForMovie(movieId, castEntities)  // Operation 4
```

**Problem:**
If the app crashes between operations, database ends up in inconsistent state:
- Scenario 1: Details saved, but videos failed → Movie has no trailers
- Scenario 2: Details + videos saved, cast failed → Movie has no cast
- Scenario 3: Old videos deleted, but insert failed → Movie lost trailers

**Impact:**
- Data corruption on app crash or error
- Difficult to recover from partial failures
- Users might see incomplete movie details

**Recommendation:**
Wrap in database transaction:

```kotlin
// Add transaction method to local data source
interface MoviesLocalDataSource {
    suspend fun <T> withTransaction(block: suspend () -> T): T
}

// Implementation
class MoviesLocalDataSourceImpl(
    private val database: Database
) : MoviesLocalDataSource {
    override suspend fun <T> withTransaction(block: suspend () -> T): T {
        return database.withTransaction(block)
    }
}

// Repository uses transaction
override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> = coroutineScope {
    // ... fetch from network ...

    // ✅ Atomic save - all or nothing
    localDataSource.withTransaction {
        localDataSource.insertMovieDetails(detailsEntity)

        localDataSource.deleteVideosForMovie(movieId)
        if (trailers.isNotEmpty()) {
            localDataSource.insertVideos(videoEntities)
        }

        localDataSource.replaceCastForMovie(movieId, castEntities)
    }

    // Return result
}
```

**Note:** Room's `@Transaction` annotation only works on DAO methods, not across multiple DAO calls. You need database-level transaction:

```kotlin
// Room Database
@Database(
    entities = [MovieEntity::class, MovieDetailsEntity::class, VideoEntity::class, CastMemberEntity::class],
    version = 1
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieDetailsDao(): MovieDetailsDao
}

// Extension for transactions
suspend fun <T> RoomDatabase.withTransaction(block: suspend () -> T): T {
    return withTransaction {
        runBlocking { block() }  // Room provides withTransaction extension
    }
}
```

---

### 2.4 Error Handling Inconsistencies

#### 🟡 MODERATE: Inconsistent Error Handling Strategy

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/movies/MoviesRepositoryImpl.kt`

**Violation:**
Different error handling for similar operations:

```kotlin
// Lines 162-165: Movie details error → fail entire operation
val details = when (detailsResult) {
    is AppResult.Success -> detailsResult.data
    is AppResult.Error -> return@coroutineScope detailsResult  // ❌ Fails completely
}

// Lines 171-181: Video error → graceful degradation
val trailers = when (videosResult) {
    is AppResult.Success -> { /* ... */ }
    is AppResult.Error -> emptyList()  // ❌ Silently ignores error
}

// Lines 184-192: Cast error → graceful degradation
val remoteCast = when (creditsResult) {
    is AppResult.Success -> { /* ... */ }
    is AppResult.Error -> emptyList()  // ❌ Silently ignores error
}
```

**Analysis:**
- Movie details failure → user sees error screen
- Video failure → user sees no trailers, no indication of error
- Cast failure → user sees no cast, no indication of error

Inconsistent UX: user doesn't know if there are genuinely no trailers/cast, or if loading failed.

**Impact:**
- Confusing user experience
- Debugging difficulty (errors silently swallowed)
- No way to retry failed trailer/cast loading
- Logs might not capture these partial failures

**Recommendation:**
Make error handling explicit and consistent:

```kotlin
// Domain model should include error states
data class MovieDetails(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val trailers: LoadableData<List<Video>>,  // ✅ Explicit loading state
    val cast: LoadableData<List<CastMember>>,
    // ...
)

sealed class LoadableData<out T> {
    object Loading : LoadableData<Nothing>()
    data class Success<T>(val data: T) : LoadableData<T>()
    data class Error(val message: String) : LoadableData<Nothing>()
}

// Repository returns partial success
override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> = coroutineScope {
    // Fetch in parallel
    val detailsDeferred = async { remoteDataSource.getMovieDetails(movieId, language) }
    val videosDeferred = async { remoteDataSource.getMovieVideos(movieId, language) }
    val creditsDeferred = async { remoteDataSource.getMovieCredits(movieId, language) }

    val detailsResult = detailsDeferred.await()

    // ✅ Details are required, fail if not available
    val details = when (detailsResult) {
        is AppResult.Success -> detailsResult.data
        is AppResult.Error -> return@coroutineScope detailsResult
    }

    val videosResult = videosDeferred.await()
    val creditsResult = creditsDeferred.await()

    // ✅ Trailers and cast are optional, but errors are preserved
    val trailers = when (videosResult) {
        is AppResult.Success -> LoadableData.Success(
            videosResult.data.results.map { it.toDomain() }
        )
        is AppResult.Error -> LoadableData.Error(videosResult.message)
    }

    val cast = when (creditsResult) {
        is AppResult.Success -> LoadableData.Success(
            creditsResult.data.cast.map { it.toDomain() }
        )
        is AppResult.Error -> LoadableData.Error(creditsResult.message)
    }

    AppResult.Success(
        MovieDetails(
            id = details.id,
            title = details.title,
            trailers = trailers,
            cast = cast,
            // ...
        )
    )
}

// UI can show partial success with error indicators
@Composable
fun MovieDetailsScreen(details: MovieDetails) {
    // ...

    when (val trailersState = details.trailers) {
        is LoadableData.Success -> TrailersSection(trailersState.data)
        is LoadableData.Error -> ErrorCard(
            message = "Failed to load trailers",
            onRetry = { /* retry trailers only */ }
        )
        LoadableData.Loading -> LoadingIndicator()
    }
}
```

---

### 2.5 Tight Coupling Issues

#### 🟡 MODERATE: LanguageChangeCoordinator Tightly Coupled to Repositories

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/LanguageChangeCoordinatorsInitializer.kt`

**Violation:**
Coordinator initialization hardcodes specific repositories:

```kotlin
class LanguageChangeCoordinatorsInitializer(
    appSettingsPreferences: AppSettingsPreferences,
    appDispatchers: AppDispatchers,
    moviesRepository: MoviesRepository,      // ❌ Coupled to specific repos
    tvShowsRepository: TvShowsRepository,    // ❌ Adding new feature requires modification
) {
    init {
        // Movies coordinator
        LanguageChangeCoordinator(
            appSettingsPreferences = appSettingsPreferences,
            scope = CoroutineScope(appDispatchers.default),
            onLanguageChange = {
                moviesRepository.clearAndReload()
            }
        )

        // TV Shows coordinator
        LanguageChangeCoordinator(
            appSettingsPreferences = appSettingsPreferences,
            scope = CoroutineScope(appDispatchers.default),
            onLanguageChange = {
                tvShowsRepository.clearAndReload()
            }
        )
    }
}
```

**Impact:**
- Adding a new feature (e.g., "Books") requires modifying this class
- Violates Open/Closed Principle
- DI module must inject every repository
- Cannot dynamically register listeners

**Recommendation:**
Use observer pattern or event bus:

```kotlin
// Define a common interface for language-aware components
interface LanguageChangeListener {
    suspend fun onLanguageChanged(newLanguage: String)
}

// Repositories implement the interface
class MoviesRepositoryImpl(...) : MoviesRepository, LanguageChangeListener {
    override suspend fun onLanguageChanged(newLanguage: String) {
        clearAndReload()
    }
}

class TvShowsRepositoryImpl(...) : TvShowsRepository, LanguageChangeListener {
    override suspend fun onLanguageChanged(newLanguage: String) {
        clearAndReload()
    }
}

// Coordinator manages all listeners
class LanguageChangeCoordinator(
    private val appSettingsPreferences: AppSettingsPreferences,
    private val scope: CoroutineScope
) {
    private val listeners = mutableListOf<LanguageChangeListener>()

    fun registerListener(listener: LanguageChangeListener) {
        listeners.add(listener)
    }

    fun start() {
        scope.launch {
            appSettingsPreferences.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1)
                .collect { newLanguage ->
                    listeners.forEach { it.onLanguageChanged(newLanguage) }
                }
        }
    }
}

// DI module dynamically registers listeners
val dataModule = module {
    single { LanguageChangeCoordinator(get(), CoroutineScope(get<AppDispatchers>().default)) }

    single<MoviesRepository> {
        MoviesRepositoryImpl(...).also { repo ->
            get<LanguageChangeCoordinator>().registerListener(repo)  // ✅ Auto-register
        }
    }

    single<TvShowsRepository> {
        TvShowsRepositoryImpl(...).also { repo ->
            get<LanguageChangeCoordinator>().registerListener(repo)  // ✅ Auto-register
        }
    }
}

// Start coordinator
class App {
    fun onCreate() {
        initKoin()
        koin.get<LanguageChangeCoordinator>().start()
    }
}
```

Now adding new features doesn't require modifying the coordinator!

---

#### 🟢 MINOR: Search Strategy Implementations Are Thin Wrappers

**Already covered in OCP section** - SearchStrategy pattern may be over-engineered since strategies just delegate to repository.

---

## 3. Additional Architectural Concerns

### 3.1 Pagination State Management

#### 🟡 MODERATE: Pagination State Separated from Movie Data

**Location:**
- Movie data: `core/database` (Room database)
- Pagination state: `core/datastore` (DataStore preferences)

**Observation:**
Pagination state (current page, total pages) is stored separately from movie data:

```kotlin
// Movie entities in Room database
@Entity
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val timestamp: Long
)

// Pagination state in DataStore
data class PaginationState(
    val currentPage: Int,
    val totalPages: Int
)
```

**Potential Issue:**
- Data can become inconsistent if movies are cleared but pagination state isn't
- Two sources of truth for related data
- No atomic operation to clear both

**Current Handling:**
```kotlin
// MoviesRepositoryImpl.kt:244-248
override suspend fun clearAndReload() {
    paginationPreferences.clearAllPaginationState()  // Clear pagination
    moviesLocalDataSource.clearAllMovies()           // Clear movies
    // ⚠️ Not atomic - could fail between these operations
}
```

**Recommendation:**
Consider storing pagination metadata with movies in Room:

```kotlin
@Entity
data class CategoryMetadataEntity(
    @PrimaryKey val category: String,
    val currentPage: Int,
    val totalPages: Int,
    val lastUpdated: Long
)

@Dao
interface CategoryMetadataDao {
    @Query("SELECT * FROM CategoryMetadataEntity WHERE category = :category")
    suspend fun getMetadata(category: String): CategoryMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMetadata(metadata: CategoryMetadataEntity)

    @Query("DELETE FROM CategoryMetadataEntity")
    suspend fun clearAll()
}

// Atomic clear operation
@Transaction
suspend fun clearAllMoviesAndMetadata() {
    movieDao.clearAllMovies()
    metadataDao.clearAll()
}
```

---

### 3.2 Filename Typo

#### 🟢 MINOR: MovieDetailsViewModel Filename Misspelled

**Location:** `features/movies/src/commonMain/kotlin/com/elna/moviedb/feature/movies/ui/movie_details/MovieDetialsViewModel.kt`

**Issue:**
Filename is `MovieDetialsViewModel.kt` instead of `MovieDetailsViewModel.kt`

**Impact:**
- Minor - doesn't affect functionality
- Could cause confusion when searching for file
- Inconsistent with class name inside file

**Recommendation:**
Rename file to match class name:
```bash
git mv MovieDetialsViewModel.kt MovieDetailsViewModel.kt
```

---

### 3.3 Language Coordinator Timing Issue

#### 🟡 MINOR: drop(1) Could Miss Changes During App Startup

**Location:** `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/LanguageChangeCoordinator.kt:29`

**Potential Issue:**
```kotlin
init {
    scope.launch {
        appSettingsPreferences.getAppLanguageCode()
            .distinctUntilChanged()
            .drop(1)  // ⚠️ Skips first emission
            .collect {
                onLanguageChange()
            }
    }
}
```

**Scenario:**
1. App starts
2. User immediately changes language before flow emits first value
3. `drop(1)` might skip the actual change

**Likelihood:** Very low, but theoretically possible

**Recommendation:**
Use a more robust approach:

```kotlin
init {
    var isFirstEmission = true
    scope.launch {
        appSettingsPreferences.getAppLanguageCode()
            .distinctUntilChanged()
            .collect { newLanguage ->
                if (isFirstEmission) {
                    isFirstEmission = false
                    return@collect  // Skip initial value
                }
                onLanguageChange()
            }
    }
}
```

Or track previous value explicitly:

```kotlin
init {
    var previousLanguage: String? = null
    scope.launch {
        appSettingsPreferences.getAppLanguageCode()
            .collect { newLanguage ->
                if (previousLanguage != null && previousLanguage != newLanguage) {
                    onLanguageChange()
                }
                previousLanguage = newLanguage
            }
    }
}
```

---

## 4. Summary of Violations by Severity

### 🔴 CRITICAL Violations (Must Fix)

1. **MoviesRepositoryImpl Multiple Responsibilities (SRP)**
   - File: `core/data/movies/MoviesRepositoryImpl.kt`
   - Impact: Difficult to test, high complexity, poor reusability
   - Fix: Extract caching strategy, network coordination, error handling

2. **MoviesLocalDataSource Interface Too Large (SRP + ISP)**
   - File: `core/database/MoviesLocalDataSource.kt`
   - Impact: Forced dependencies, testing overhead
   - Fix: Split into focused interfaces

3. **Missing Use Case Layer (Clean Architecture)**
   - Impact: Business logic scattered, not reusable, testing difficult
   - Fix: Introduce domain/use case layer

4. **Database Transaction Atomicity Issues**
   - File: `core/data/movies/MoviesRepositoryImpl.kt:194-217`
   - Impact: Data corruption on failures
   - Fix: Wrap multi-operation saves in transactions

---

### 🟡 MODERATE Violations (Should Fix)

1. **SearchRepositoryImpl Code Duplication (SRP)**
   - File: `core/data/search/SearchRepositoryImpl.kt`
   - Impact: Maintenance burden, duplicated error handling
   - Fix: Extract common pattern

2. **MoviesViewModel Too Many Concerns (SRP)**
   - File: `features/movies/ui/movies/MoviesViewModel.kt`
   - Impact: Fat ViewModel, business logic in presentation layer
   - Fix: Extract to use cases

3. **MovieDetailsDao Multiple Concerns (ISP)**
   - File: `core/database/MovieDetailsDao.kt`
   - Impact: Forced dependencies on unneeded operations
   - Fix: Split into MovieDetailsDao, MovieVideosDao, MovieCastDao

4. **Data Transformation in Repository Layer (Clean Arch)**
   - File: `core/data/movies/MoviesRepositoryImpl.kt`
   - Impact: Data layer coupled to UI concerns
   - Fix: Move URL transformation to presentation layer

5. **DTO Mapping in Repository (Clean Arch)**
   - File: `core/data/movies/MoviesRepositoryImpl.kt:100`
   - Impact: Mixing responsibilities
   - Fix: Move mapping to data source

6. **Inconsistent Error Handling Strategy**
   - File: `core/data/movies/MoviesRepositoryImpl.kt`
   - Impact: Confusing UX, silent failures
   - Fix: Use LoadableData for optional data

7. **LanguageChangeCoordinator Tight Coupling**
   - File: `core/data/LanguageChangeCoordinatorsInitializer.kt`
   - Impact: Not extensible, violates OCP
   - Fix: Use observer pattern

8. **Pagination State Separated from Data**
   - Files: `core/database` vs `core/datastore`
   - Impact: Potential inconsistency
   - Fix: Store pagination metadata in Room

---

### 🟢 MINOR Violations (Nice to Fix)

1. **SearchViewModel Strategy Mapping (OCP)**
   - File: `features/search/ui/SearchViewModel.kt:45-52`
   - Impact: Adding filters requires modification
   - Fix: Use factory pattern

2. **SearchStrategy Pattern Over-Engineered (OCP)**
   - File: `features/search/strategy/SearchStrategy.kt`
   - Impact: Unnecessary complexity
   - Fix: Consider removing pattern

3. **LanguageProvider Not an Interface (DIP)**
   - File: `core/data/util/LanguageProvider.kt`
   - Impact: Limited testability
   - Fix: Extract interface

4. **Language Coordinator drop(1) Timing**
   - File: `core/data/LanguageChangeCoordinator.kt:29`
   - Impact: Theoretical edge case
   - Fix: Track previous value explicitly

5. **Filename Typo**
   - File: `MovieDetialsViewModel.kt`
   - Impact: Confusion
   - Fix: Rename file

---

## 5. Recommended Refactoring Priority

### Phase 1: Critical Fixes (1-2 weeks)

1. **Introduce Use Case Layer**
   - Create `domain/usecase` module
   - Extract business logic from repositories and ViewModels
   - Start with most complex flows (GetMovieDetails, LoadMoviesNextPage)

2. **Fix Database Transaction Atomicity**
   - Add transaction wrapper to MoviesLocalDataSource
   - Wrap movie details saves in atomic transactions

3. **Split MoviesLocalDataSource**
   - Create focused interfaces (MoviesListDataSource, MovieDetailsDataSource, etc.)
   - Update repository to use specific interfaces

---

### Phase 2: Moderate Improvements (2-3 weeks)

1. **Refactor MoviesRepositoryImpl**
   - Extract CachingStrategy
   - Extract MovieDetailsAggregator
   - Simplify repository to orchestration only

2. **Fix Error Handling Consistency**
   - Introduce LoadableData for optional fields
   - Update domain models
   - Update UI to show partial loading states

3. **Refactor SearchRepository**
   - Extract common search pattern
   - Reduce code duplication

4. **Decouple Language Coordinator**
   - Implement observer pattern
   - Auto-register language listeners in DI

---

### Phase 3: Minor Improvements (1 week)

1. **Move Data Transformations**
   - Move URL transformation to presentation layer
   - Move DTO mapping to data sources

2. **Fix Minor Issues**
   - Rename MovieDetailsViewModel file
   - Make LanguageProvider an interface
   - Fix language coordinator timing

3. **Consolidate Pagination State**
   - Move pagination metadata to Room
   - Ensure atomic clear operations

---

## 6. Positive Patterns to Maintain

### ✅ Excellent Use of Design Patterns

1. **Open/Closed Principle with Enums**
   - MovieCategory and SearchFilter with associated data
   - Enables extension without modification

2. **MVI/UDF Pattern**
   - Unidirectional data flow
   - Immutable state with sealed classes
   - Clear separation of events and state

3. **Repository Pattern**
   - Clear abstraction between data sources and business logic
   - Interface-based design

4. **Type-Safe Error Handling**
   - AppResult sealed interface
   - Compile-time safety for error cases

5. **Dependency Injection with Koin**
   - Interface-based dependencies
   - Modular DI configuration

6. **Offline-First Architecture**
   - Cache-first strategy
   - Graceful degradation
   - Flow-based reactive data

---

## 7. Testing Recommendations

To ensure SOLID principles and Clean Architecture are maintained:

### Unit Tests Needed

1. **Use Case Tests** (once implemented)
   ```kotlin
   class GetMovieDetailsUseCaseTest {
       @Test
       fun `should filter and sort trailers correctly`() { }

       @Test
       fun `should handle missing trailers gracefully`() { }
   }
   ```

2. **Repository Tests**
   ```kotlin
   class MoviesRepositoryImplTest {
       @Test
       fun `should return cached data when available`() { }

       @Test
       fun `should fetch from network when cache is empty`() { }

       @Test
       fun `should save to cache after network fetch`() { }
   }
   ```

3. **ViewModel Tests**
   ```kotlin
   class MoviesViewModelTest {
       @Test
       fun `should show error screen when initial load fails`() { }

       @Test
       fun `should show snackbar when pagination fails with existing data`() { }
   }
   ```

### Integration Tests Needed

1. **Database Transaction Tests**
   ```kotlin
   @Test
   fun `should rollback on failure during movie details save`() { }
   ```

2. **Language Change Tests**
   ```kotlin
   @Test
   fun `should clear all data when language changes`() { }
   ```

---

## 8. Conclusion

The CMP MovieDB project demonstrates **strong architectural foundations** with excellent use of:
- Design patterns (MVI, Repository, Strategy)
- Open/Closed Principle through enum-based extensibility
- Dependency Inversion with interface-based design
- Type-safe error handling

However, critical improvements are needed in:
- **Introducing a Use Case layer** to properly separate business logic
- **Splitting large interfaces** to adhere to SRP and ISP
- **Ensuring database atomicity** for multi-operation saves
- **Moving data transformations** to appropriate layers

Following the recommended refactoring phases will significantly improve:
- Testability
- Maintainability
- Extensibility
- Clean Architecture compliance

The codebase is well-positioned for these improvements due to its existing strong patterns and clear separation of concerns.

---

**Total Violations Found:**
- 🔴 Critical: 4
- 🟡 Moderate: 8
- 🟢 Minor: 5

**Total Positive Patterns:** 6

**Overall Grade: B+** (Strong foundation with room for improvement)