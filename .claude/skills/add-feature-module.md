# add-feature-module

## Description
Scaffold a new feature module in the Kotlin Multiplatform project with the standard structure, build configuration, Koin DI, and project registration.

## Usage
```
/add-feature-module [module_name]
```

If `module_name` is not provided as an argument, the skill will prompt for it.

## Instructions

When this skill is invoked:

1. **Extract or prompt for input**:
   - If the user provided `module_name` as an argument, use it directly
   - Otherwise, use AskUserQuestion to prompt for:
     - Module name (kebab-case format, e.g., `watchlist`, `favorites`, `notifications`)

2. **Determine the package name**:
   - Convert kebab-case to dotted package: e.g., `watchlist` -> `com.elna.moviedb.feature.watchlist`
   - For multi-word names: `my-feature` -> `com.elna.moviedb.feature.myfeature`

3. **Create the module directory structure**:
   ```
   features/<module_name>/
   ├── build.gradle.kts
   └── src/
       ├── commonMain/
       │   └── kotlin/com/elna/moviedb/feature/<module_name>/
       │       ├── di/
       │       │   └── <ModuleName>Module.kt
       │       ├── model/
       │       ├── presentation/
       │       │   ├── <ModuleName>Screen.kt
       │       │   ├── <ModuleName>ViewModel.kt
       │       │   └── <ModuleName>UiState.kt
       │       └── navigation/
       │           └── <ModuleName>Route.kt
       ├── androidMain/
       │   └── kotlin/com/elna/moviedb/feature/<module_name>/
       └── iosMain/
           └── kotlin/com/elna/moviedb/feature/<module_name>/
   ```

4. **Create `build.gradle.kts`**:
   ```kotlin
   plugins {
       alias(libs.plugins.moviedb.kotlinMultiplatform)
       alias(libs.plugins.moviedb.composeMultiplatform)
       alias(libs.plugins.kotlinSerialization)
   }

   kotlin {
       sourceSets {
           commonMain.dependencies {
               implementation(projects.core.common)
               implementation(projects.core.model)
               implementation(projects.core.data)
               implementation(projects.core.ui)

               implementation(libs.koin.compose.viewmodel)
               implementation(libs.navigation3.runtime)
           }
       }
   }
   ```
   - Adjust dependencies based on user requirements (e.g., add `projects.core.network` if needed)

5. **Create the Koin DI module** (`di/<ModuleName>Module.kt`):
   ```kotlin
   package com.elna.moviedb.feature.<module_name>.di

   import com.elna.moviedb.feature.<module_name>.presentation.<ModuleName>ViewModel
   import org.koin.core.module.dsl.viewModelOf
   import org.koin.dsl.module

   val <moduleName>Module = module {
       viewModelOf(::<ModuleName>ViewModel)
   }
   ```

6. **Create the navigation route** (`navigation/<ModuleName>Route.kt`):
   ```kotlin
   package com.elna.moviedb.feature.<module_name>.navigation

   import kotlinx.serialization.Serializable

   @Serializable
   data object <ModuleName>Route
   ```
   - Use `data class` instead if the route needs parameters

7. **Create the UI state** (`presentation/<ModuleName>UiState.kt`):
   ```kotlin
   package com.elna.moviedb.feature.<module_name>.presentation

   data class <ModuleName>UiState(
       val isLoading: Boolean = false,
       val errorMessage: String? = null,
   )
   ```

8. **Create the ViewModel** (`presentation/<ModuleName>ViewModel.kt`):
   ```kotlin
   package com.elna.moviedb.feature.<module_name>.presentation

   import androidx.lifecycle.ViewModel
   import kotlinx.coroutines.flow.MutableStateFlow
   import kotlinx.coroutines.flow.StateFlow
   import kotlinx.coroutines.flow.asStateFlow

   class <ModuleName>ViewModel : ViewModel() {
       private val _uiState = MutableStateFlow(<ModuleName>UiState())
       val uiState: StateFlow<<ModuleName>UiState> = _uiState.asStateFlow()
   }
   ```

9. **Create the Screen composable** (`presentation/<ModuleName>Screen.kt`):
   ```kotlin
   package com.elna.moviedb.feature.<module_name>.presentation

   import androidx.compose.runtime.Composable
   import androidx.compose.runtime.getValue
   import androidx.lifecycle.compose.collectAsStateWithLifecycle
   import org.koin.compose.viewmodel.koinViewModel

   @Composable
   fun <ModuleName>Screen(
       viewModel: <ModuleName>ViewModel = koinViewModel(),
   ) {
       val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   }
   ```

10. **Register the module in `settings.gradle.kts`**:
    - Add `include(":features:<module_name>")` alongside existing feature includes

11. **Add dependency in `composeApp/build.gradle.kts`**:
    - Add `implementation(projects.features.<moduleName>)` in the commonMain dependencies

12. **Register the Koin module**:
    - Read the main Koin setup file and add `<moduleName>Module` to the modules list

13. **Add to navigation graph**:
    - Read `composeApp/src/commonMain/kotlin/com/elna/moviedb/navigation/RootNavGraph.kt`
    - Add a new entry for the route following the existing pattern

14. **Output summary**:
    - List all created files
    - Remind about next steps: adding screen content, connecting to repositories, adding navigation triggers

## Notes
- Follow the existing MVI pattern: ViewModel manages UI state, Screen observes it
- Use convention plugins (`moviedb.kotlinMultiplatform`, `moviedb.composeMultiplatform`) for consistent configuration
- All user-facing strings must use string resources (invoke `/add-strings` as needed)
- Navigation uses Navigation 3 with `@Serializable` routes
- ViewModels are injected via `koinViewModel()` in Composables