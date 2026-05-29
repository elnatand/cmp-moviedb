# add-category

## Description
Add a new movie or TV show category to the app. Thanks to the category abstraction pattern, this only requires adding an enum value, an API endpoint, and a string resource.

## Usage
```
/add-category [type] [category_name] [display_name]
```

- `type`: `movie` or `tvshow`
- `category_name`: PascalCase enum value name (e.g., `Upcoming`, `AiringToday`)
- `display_name`: English display text (e.g., `"Upcoming"`, `"Airing Today"`)

If arguments are not provided, the skill will prompt for them.

## Instructions

When this skill is invoked:

1. **Extract or prompt for input**:
   - If the user provided all arguments, use them directly
   - Otherwise, use AskUserQuestion to prompt for:
     - Type: movie or TV show
     - Category name in PascalCase (e.g., `Upcoming`, `AiringToday`)
     - English display name (e.g., `"Upcoming"`, `"Airing Today"`)

2. **Add the enum value**:

   For **movies**, read and edit:
   `core/model/src/commonMain/kotlin/com/elna/moviedb/core/model/MovieCategory.kt`
   - Add the new enum value following the existing pattern
   - Each enum value has a string resource reference for its display name

   For **TV shows**, read and edit:
   `core/model/src/commonMain/kotlin/com/elna/moviedb/core/model/TvShowCategory.kt`
   - Add the new enum value following the existing pattern

3. **Add the string resource**:
   - Invoke `/add-strings` with an appropriate key (e.g., `category_upcoming`) and the English display name
   - Wait for the skill to complete before continuing

4. **Add the API endpoint**:

   Read the network client to understand the existing pattern:
   `core/network/src/commonMain/kotlin/com/elna/moviedb/core/network/`

   - Add the new TMDB API endpoint method following existing patterns
   - Common TMDB endpoints:
     - Movies: `/movie/now_playing`, `/movie/popular`, `/movie/top_rated`, `/movie/upcoming`
     - TV: `/tv/airing_today`, `/tv/on_the_air`, `/tv/popular`, `/tv/top_rated`

5. **Update the repository mapping**:

   Read the relevant repository implementation to find where categories are mapped to API calls:

   For **movies**:
   `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/`

   For **TV shows**:
   `core/data/src/commonMain/kotlin/com/elna/moviedb/core/data/`

   - Add a mapping from the new category enum to the new API endpoint
   - Follow the existing `when` expression or map pattern

6. **Verify no ViewModel/UI changes needed**:
   - Thanks to the category abstraction pattern, ViewModels iterate over `Category.entries`
   - No changes should be needed in ViewModel or UI code
   - If the existing code uses exhaustive `when` on categories, Kotlin will enforce adding the new branch at compile time

7. **Output summary**:
   - Confirm the category was added
   - List modified files
   - Note that no ViewModel/UI changes were needed (demonstrating the Open-Closed Principle)
   - Suggest running a build to verify: `./gradlew compileCommonMainKotlinMetadata`

## Notes
- The category abstraction pattern means adding a category is a minimal change
- ViewModels use `Category.entries` to iterate, so new categories are picked up automatically
- Map-based state (`moviesByCategory: Map<MovieCategory, List<Movie>>`) handles new categories without code changes
- String resources are required for category display names (never hardcode)
- If the TMDB API endpoint doesn't exist yet, check the [TMDB API docs](https://developer.themoviedb.org/reference) for available endpoints