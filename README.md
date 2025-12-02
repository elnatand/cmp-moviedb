# CMP MovieDB

A Kotlin Multiplatform Mobile (KMP) application built with Compose Multiplatform (CMP) that displays movies, TV shows and Actors information. The app targets both Android and iOS platforms with shared business logic and UI components using a modular architecture approach.

## 🏗️ Architecture

The project follows Clean Architecture principles with a **feature-based multi-module architecture**:

- **Multi-Module Design**: Separated into core modules and feature modules for scalability
- **MVI Pattern**: Model-View-Intent with unidirectional data flow (UDF)
- **Repository Pattern**: Data layer abstraction across modules
- **Dependency Injection**: Koin for DI coordination across all modules
- **Compose Multiplatform**: Shared UI components in dedicated modules
- **Room Database**: Local database management
- **DataStore**: Typed data storage for preferences and pagination state persistence

## 📱 Features

### Currently Implemented
- **Movies**: Browse popular, top-rated, and now-playing movies with infinite scroll pagination
- **Movie Details**: View comprehensive movie information including:
  - Overview, genres, runtime, release date, ratings
  - Budget and revenue with formatted numbers
  - Production companies and countries
  - **Cast & Crew**: Browse cast members with profile images and character names
  - **Trailers**: Watch official trailers and videos
- **TV Shows**: Explore popular, top-rated, and on-the-air TV shows with pagination support
- **TV Show Details**: View detailed information including:
  - Series information (seasons, episodes, status)
  - Networks, languages, and production details
  - **Cast Section**: Browse TV show cast with character information
  - **Trailers**: Watch official trailers and promotional videos
  - Episode runtime and air dates
- **Person Details**: View cast and crew member information with:
  - Biography and personal information (birthdate, birthplace)
  - Filmography with work credits
  - Profile images and known roles
- **Search**: Multi-type search functionality across movies, TV shows, and people with filter options
- **Profile**: User profile management with language and theme selection
- **Cross-platform**: Shared multi-module codebase for Android and iOS
- **Offline Support (Currently for movies section only)**: Local database caching with Room for movies, movie details, cast members, and trailers
- **Persistent Pagination**: Pagination state persists across app restarts via DataStore
- **Internationalization**: Support for 4 languages (English, Hebrew, Hindi, Arabic) with dynamic switching and automatic content refresh
- **Dark Mode Support**: System-based dark/light theme with Material 3
- **Modern UI**: Material 3 design system with tile-based layouts and responsive components

## 🌍 Language Support

The app supports **4 languages** with full UI localization:

- **English** (Default)
- **العربية (Arabic)** - Right-to-left (RTL) layout support
- **עברית (Hebrew)** - Right-to-left (RTL) layout support
- **हिन्दी (Hindi)**

### Features
- Dynamic language switching from the Profile screen
- Automatic content refresh when language changes
- Language-aware cache invalidation
- RTL layout support for Arabic and Hebrew
- All string resources fully localized

## 📁 Project Structure

```
cmp-moviedb/
├── composeApp/           # Main application module
│   ├── androidMain/      # Android-specific code (MainActivity, DI)
│   ├── commonMain/       # Shared app code (App.kt, navigation, DI)
│   └── iosMain/          # iOS-specific code (MainViewController, DI)
│
├── core/                 # Core shared modules
│   ├── common/           # Common utilities and coroutine dispatchers
│   ├── data/             # Repository implementations and data layer
│   ├── database/         # Room database with cross-platform drivers
│   ├── datastore/        # DataStore preferences for pagination state & settings
│   ├── model/            # Domain models (Movie, TvShow, AppResult)
│   ├── network/          # Ktor HTTP client and TMDB API integration
│   └── ui/               # Shared UI components and design system
│
├── features/             # Feature-specific modules
│   ├── movies/           # Movies list and details screens
│   ├── tv-shows/         # TV shows list and details screens
│   ├── person/           # Person details screen (cast/crew info)
│   ├── search/           # Search functionality
│   └── profile/          # User profile and settings screen
│
├── build-logic/          # Custom Gradle convention plugins
├── iosApp/               # iOS app wrapper with Xcode project
├── gradle/               # Gradle configuration and version catalog
└── Configuration files   # Build scripts, API keys, properties
```

## 🛠️ Technology Stack

### Shared
- **Kotlin Multiplatform** - Multi-platform development
- **Compose Multiplatform** - UI framework
- **Koin** - Dependency injection with Compose and ViewModel support
- **Room** - Local database with SQLite bundled driver
- **DataStore** - Typed data storage for preferences and pagination state
- **Ktor** - HTTP client for API calls with OkHttp (Android) and Darwin (iOS) engines
- **Kotlin Coroutines & Flow** - Asynchronous programming and reactive streams
- **Coil** - Image loading with Compose integration, Ktor network driver, and SVG support
- **Kotlinx Serialization** - JSON serialization for API responses
- **Navigation 3** - Navigation 3 framework with Koin integration
- **Material 3** - Design system components
- **KSP** - Kotlin Symbol Processing for Room code generation

### Android
- **Jetpack Compose** - UI toolkit
- **OkHttp** - Network engine for Ktor

### iOS
- **SwiftUI Integration** - iOS native integration
- **Darwin Engine** - Native networking for Ktor

## 🚀 Getting Started

### Prerequisites
- **Android Studio** - Ladybug or later with KMP plugin
- **Xcode 15+** (for iOS development)
- **JDK 17-21** 
- **Kotlin Multiplatform Mobile plugin**
- **TMDB API Key** (see setup instructions below)
- **Minimum SDK**: Android 24, iOS 13+
- **Target SDK**: Android 36

### 🔑 API Key Setup

This app uses The Movie Database (TMDB) API. You'll need to obtain an API key and configure it for both Android and iOS platforms.

#### Step 1: Get TMDB API Key

1. **Create TMDB Account**
   - Visit [The Movie Database](https://www.themoviedb.org/)
   - Sign up for a free account or log in if you already have one

2. **Request API Key**
   - Go to [TMDB Developer Portal](https://developer.themoviedb.org/reference/intro/getting-started)
   - Navigate to your account settings
   - Click on "API" in the sidebar
   - Click "Create" and select "Developer"
   - Fill out the application form:
     - Application Name: `CMP MovieDB` (or your preferred name)
     - Application Summary: Brief description of your movie app
     - Application URL: Your app's URL or GitHub repository
   - Agree to the terms and submit

3. **Get Your API Key**
   - Once approved, you'll receive your API key
   - Copy the **API Key (v3 auth)** - this is what you'll use

#### Step 2: Configure Android (secrets.properties)

1. **Create secrets.properties file** in the project root directory:
   ```bash
   touch secrets.properties
   ```

2. **Add your API key** to `secrets.properties`:
   ```properties
   TMDB_API_KEY=your_actual_api_key_here
   ```

   Replace `your_actual_api_key_here` with your actual TMDB API key.

3. **Verify .gitignore** - The file should already be excluded from Git:
   ```gitignore
   /secrets.properties
   ```

#### Step 3: Configure iOS (Secrets.plist)

1. **Create Secrets.plist** from the template:
   ```bash
   cp iosApp/iosApp/Secrets.plist.template iosApp/iosApp/Secrets.plist
   ```

2. **Edit Secrets.plist** and replace the placeholder:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
   <plist version="1.0">
   <dict>
       <key>tmdbApiKey</key>
       <string>your_actual_api_key_here</string>
   </dict>
   </plist>
   ```

3. **Add to Xcode project** (if not already added):
   - Open the iOS project in Xcode
   - Drag `Secrets.plist` into the project navigator
   - Ensure it's added to the target's "Copy Bundle Resources" phase

#### Step 4: Verify Setup

Both files should contain the same API key but in different formats:

**secrets.properties**:
```properties
TMDB_API_KEY=abcd1234567890efgh
```

**Secrets.plist**:
```xml
<key>tmdbApiKey</key>
<string>abcd1234567890efgh</string>
```

> ⚠️ **Important**: Never commit these files to version control. They are already excluded in `.gitignore`.

### Running the Project

#### Android
```bash
./gradlew :composeApp:assembleDebug
```

#### iOS
1. Open the project in Android Studio
2. Select iOS target from the run configuration
3. Run the project

### Building
```bash
# Clean and build all modules
./gradlew clean build

# Build specific module
./gradlew :composeApp:build
```


## 🏛️ Architecture Overview

### Repository Pattern (Simplified)
The project uses a simplified repository pattern where:
- **Repository**: Passive data provider that returns `Flow<List<T>>` and handles cache invalidation
- **ViewModel**: Active state coordinator that manages loading/error states and UI logic
- **Clear Separation**: Repository maintains data integrity, ViewModel manages UI state

### Data Storage Strategy
- **Movies**: Offline-first with Room database for persistent caching
- **TV Shows**: In-memory storage with reactive StateFlow updates
- **Preferences**: DataStore for app settings and pagination state persistence
- **Cache Invalidation**: Automatic clearing of stale data on language changes

### Architecture Layers

1. **Presentation Layer** - Compose UI, ViewModels, Navigation
2. **Domain Layer** - Business logic, Use cases (Repository interfaces)
3. **Data Layer** - Repository implementations, Data sources, Network, Database

## 🔧 Development Setup

### Architecture Details
- **Multi-Module Clean Architecture** with Repository pattern across modules
- **MVI (Model-View-Intent) with Simplified Repositories**:
  - Model: Immutable UI state representing the screen
  - View: Compose UI that renders state and dispatches events
  - Intent: User events handled via `onEvent()`
  - Repository: Passive data provider returning simple `Flow<List<T>>`
  - ViewModel: Coordinates state and handles loading/error logic
  - Clear separation: Data layer maintains integrity, Presentation layer manages UI state
- **Reactive UI** with Flow-based data streaming between modules
- **Offline-First Architecture**:
  - Movies: Room database caching with reactive updates
  - TV Shows: In-memory storage with StateFlow
  - Pagination state persists across app restarts via DataStore
- **Language-Aware Cache Invalidation**: Repository observes language changes and clears stale content
- **Error Handling Strategy**:
  - Initial load errors: Block UI with error screen
  - Pagination errors: Non-blocking snackbar while keeping cached data
- **Platform-specific configs** using expect/actual pattern
- **Feature-based modular design** with clear separation of concerns
- **Module isolation** with well-defined APIs and dependency injection

### Build Configuration
- **Gradle Version Catalogs** (`gradle/libs.versions.toml`) - Centralized dependency management
- **Custom Convention Plugins** in `build-logic/convention/` module:
  - **moviedb.kotlin.multiplatform**: Configures KMP with iOS ARM64/Simulator targets
  - **moviedb.kotlin.composeMultiplatform**: Adds all Compose Multiplatform dependencies
  - **moviedb.android.library**: Standardizes Android module configuration
  - **Shared Android Config**: Compile/target SDK 36, min SDK 24
- **KSP** for Room database code generation across platforms
- **DataStore** for preferences and app state persistence
- **Compose Resources** for shared string resources with 4-language support (English, Hebrew, Hindi, Arabic)
- **Platform-specific** configurations using expect/actual pattern
- **Room Database** with SQLite bundled driver for offline support

### Troubleshooting
- **Java Version Issues**: Use JDK 17-21. Java 25 has compatibility issues
- **Build Failures**: Run `./gradlew clean` and retry
- **iOS Simulator**: Use ARM64 simulator on Apple Silicon Macs
- **API Key Issues**: Verify both `secrets.properties` and `Secrets.plist` contain valid TMDB API key

## 🎬 TMDB Attribution

This application uses the TMDB API but is not endorsed, certified, or otherwise approved by TMDB.

### Data Source
All movie and TV show data is provided by [The Movie Database (TMDB)](https://www.themoviedb.org).

## 🤖 Development Partners

This app was built with the assistance of **[Claude Code](https://claude.com/claude-code)**, Anthropic's AI-powered development tool. Having Claude Code as a development partner has been transformative - it truly feels like having a real partner throughout the entire development journey. From architecting the multi-module structure to implementing complex features like offline-first architecture and multi-language support, Claude Code has been instrumental in bringing this project to life.

**[CodeRabbit](https://coderabbit.ai)** provides wonderful automated code reviews for pull requests in this project, offering intelligent insights, best practice recommendations, and thorough code analysis that helps maintain high code quality and catch potential issues early in the development process.

![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/elnatand/cmp-moviedb)

## 📋 Development Roadmap

### Recently Completed ✨
- [x] Simplified repository architecture (Repository = data provider, ViewModel = state coordinator)
- [x] DataStore integration for persistent pagination state
- [x] Language-aware cache invalidation with LanguageChangeCoordinator
- [x] Distinct error handling for initial load vs pagination
- [x] Person details feature with cast/crew information and filmography
- [x] Search functionality with multi-type filtering (ALL, MOVIES, TV_SHOWS, PEOPLE)
- [x] Profile screen with language selection and theme toggle
- [x] Dark/light theme toggle with system default option
- [x] Movie cast and crew display with detailed information
- [x] Trailers/videos for movies and TV shows
- [x] Budget and revenue formatting with comma separators
- [x] Foreign key relationships in database entities
- [x] Platform-specific API key loading via expect/actual pattern

### Current Status ✅
- [x] Multi-module Clean Architecture with KMP
- [x] Movies feature with pagination, details, cast, trailers, and offline support
- [x] TV Shows feature with pagination, details, cast, and trailers (in-memory)
- [x] Room database for offline movie storage with 4 entity tables
- [x] DataStore for app settings and pagination state persistence
- [x] Material 3 design system implementation
- [x] Multi-language support (English, Arabic, Hebrew, Hindi) with RTL layouts
- [x] Cross-platform image loading with Coil (including SVG support)
- [x] Reactive UI with StateFlow and Compose
- [x] Clean error handling with proper UI feedback
- [x] Person/cast details with biography and work history
- [x] Multi-type search with strategy pattern
- [x] Category abstraction for extensible movie/TV show browsing

### High Priority 🚀
- [ ] Add TV Show database entities and local caching (currently only Movies cached)
- [ ] Add pull-to-refresh functionality
- [ ] Create unit tests for ViewModels and repositories
- [ ] Add ktlint code formatting configuration
- [ ] Improve search UX with debouncing and better empty states

### Medium Priority 📋
- [ ] Add favorites/watchlist feature with local storage
- [ ] Enhance loading states with skeleton screens
- [ ] Add movie/TV show reviews display
- [ ] Add similar movies/shows recommendations
- [ ] Implement network connectivity detection
- [ ] Add proguard/R8 configuration for release builds
- [ ] Performance optimizations for pagination

### Low Priority 💡
- [ ] Add user authentication and personalized recommendations
- [ ] Performance optimizations for large datasets
- [ ] Add comprehensive accessibility improvements
- [ ] Implement deep linking support
- [ ] Add CI/CD pipeline with GitHub Actions
- [ ] Add analytics and crash reporting
- [ ] Implement video player for trailers (currently external links)
