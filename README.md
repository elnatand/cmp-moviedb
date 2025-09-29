# CMP MovieDB

A **multi-module** Kotlin Multiplatform Mobile (KMP) application built with Compose Multiplatform that displays movies and TV shows information. The app targets both Android and iOS platforms with shared business logic and UI components using a modular architecture approach.

## 🏗️ Architecture

The project follows Clean Architecture principles with a **feature-based multi-module architecture**:

- **Multi-Module Design**: Separated into core modules and feature modules for scalability
- **MVVM Pattern**: ViewModels handle UI state and business logic
- **Repository Pattern**: Data layer abstraction across modules
- **Dependency Injection**: Koin for DI coordination across all modules
- **Compose Multiplatform**: Shared UI components in dedicated modules
- **Room Database**: Local database management with dedicated database module

## 📱 Features

### Currently Implemented
- **Movies**: Browse popular movies with infinite scroll pagination
- **Movie Details**: View detailed information about individual movies
- **TV Shows**: Explore popular TV shows with pagination support
- **TV Show Details**: View detailed information about individual TV shows
- **Profile**: User profile management screen
- **Cross-platform**: Shared multi-module codebase for Android and iOS
- **Offline Support**: Local database caching with Room for movies and movie details
- **Auto-pagination**: Automatic loading of next pages when scrolling to bottom
- **Error Handling**: Proper error states and loading indicators
- **Modern UI**: Material 3 design system with tile-based layouts

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
│   ├── model/            # Domain models (Movie, TvShow, AppResult)
│   ├── network/          # Ktor HTTP client and TMDB API integration
│   └── ui/               # Shared UI components and design system
│
├── features/             # Feature-specific modules
│   ├── movies/           # Movies list and details screens
│   ├── tv-shows/         # TV shows list and details screens
│   └── profile/          # User profile screen
│
├── build-logic/          # Custom Gradle convention plugins
├── iosApp/               # iOS app wrapper with Xcode project
├── gradle/               # Gradle configuration and version catalog
└── Configuration files   # Build scripts, API keys, properties
```

## 🛠️ Technology Stack

### Shared
- **Kotlin Multiplatform** - Cross-platform development
- **Compose Multiplatform** - UI framework
- **Koin** - Dependency injection
- **Room** - Local database with SQLite bundled driver
- **Ktor** - HTTP client for API calls
- **Kotlin Coroutines & Flow** - Asynchronous programming and reactive streams
- **Coil** - Image loading with Compose integration
- **Kotlinx Serialization** - JSON serialization for API responses
- **Navigation Compose** - Navigation framework
- **Material 3** - Design system components
- **KSP** - Kotlin Symbol Processing for Room code generation

### Android
- **Jetpack Compose** - UI toolkit
- **Activity Compose** - Activity integration

### iOS
- **SwiftUI Integration** - iOS native integration

## 🚀 Getting Started

### Prerequisites
- **Android Studio** - Ladybug or later with KMP plugin
- **Xcode 15+** (for iOS development)
- **JDK 17-21** (Java 25 has compatibility issues)
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
       <key>apiKey</key>
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
<key>apiKey</key>
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

## 📦 Modules Overview

### Core Modules
- **core:common** - AppDispatchers, shared utilities, and common DI module
- **core:data** - Repository implementations and data source abstractions
  - MoviesRepository & MoviesRepositoryImpl with pagination and caching
  - TvShowRepository implementation (future enhancement)
- **core:database** - Room database with platform-specific drivers
  - AppDatabase with MovieDao and MovieDetailsDao
  - SQLite bundled driver for cross-platform support
  - MoviesLocalDataSource implementation
- **core:model** - Clean architecture domain models
  - Movie, MovieDetails, TvShow, TvShowDetails
  - AppResult generic wrapper for success/error handling
- **core:network** - Network layer with Ktor client
  - MoviesRemoteDataSource and TvShowsRemoteDataSource
  - TMDB API integration with DTOs and mappers
  - Platform-specific HTTP client configurations
- **core:ui** - Shared UI components and design system
  - Material 3 design system components (AppLoader, AppErrorComponent)
  - ImageLoader utility with Coil integration
  - Navigation routes and UI extensions
  - Shared string resources with Hebrew translations

### Feature Modules
- **features:movies** - Complete movies feature implementation
  - Movies list with infinite scroll pagination
  - Movie details screen with cast and crew information
  - ViewModels with reactive state management
- **features:tv-shows** - TV shows feature (similar to movies)
  - TV shows list and details screens
  - Reactive ViewModels with StateFlow
- **features:profile** - User profile management
  - Profile screen and ViewModel

### App Module
- **composeApp** - Main application orchestration
  - Cross-platform App composable
  - Root navigation setup with bottom navigation
  - DI module coordination across features
  - Platform-specific MainActivity (Android) and MainViewController (iOS)

### Build Logic
- **build-logic:convention** - Gradle convention plugins for consistent builds
  - `moviedb.kotlin.multiplatform` - KMP configuration with iOS ARM64/Simulator
  - `moviedb.kotlin.composeMultiplatform` - Compose dependencies and resources
  - `moviedb.android.library` - Standardized Android library setup

## 🏛️ Architecture Layers

1. **Presentation Layer** - Compose UI, ViewModels, Navigation
2. **Domain Layer** - Business logic, Use cases (Repository interfaces)
3. **Data Layer** - Repository implementations, Data sources, Network, Database

## 🔧 Development Setup

### Architecture Details
- **Multi-Module Clean Architecture** with Repository pattern across modules
- **MVVM** using Compose ViewModels with StateFlow
- **Reactive UI** with Flow-based data streaming between modules
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
- **Compose Resources** for shared string resources with Hebrew translations
- **Platform-specific** configurations using expect/actual pattern
- **Room Database** with SQLite bundled driver for offline support

### Development Commands
```bash
# Build all modules
./gradlew build

# Clean build
./gradlew clean build

# Android builds
./gradlew :composeApp:assembleDebug        # Debug APK
./gradlew :composeApp:assembleRelease      # Release APK
./gradlew :composeApp:installDebug         # Install debug APK

# Build specific modules
./gradlew :core:data:build                 # Build data module
./gradlew :features:movies:build           # Build movies feature
./gradlew :core:database:build             # Build database module

# Run tests
./gradlew test                             # Run all unit tests
./gradlew :core:data:test                  # Test specific module

# Code quality (configure ktlint if needed)
./gradlew ktlintCheck                      # Check code style
./gradlew ktlintFormat                     # Auto-format code

# iOS development (requires Xcode)
# Open iosApp/iosApp.xcodeproj in Xcode or use Android Studio
```

### Troubleshooting
- **Java Version Issues**: Use JDK 17-21. Java 25 has compatibility issues
- **Build Failures**: Run `./gradlew clean` and retry
- **iOS Simulator**: Use ARM64 simulator on Apple Silicon Macs
- **API Key Issues**: Verify both `secrets.properties` and `Secrets.plist` contain valid TMDB API key

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📋 Development Roadmap

### Current Status ✅
- [x] Multi-module Clean Architecture with KMP
- [x] Movies feature with pagination and details
- [x] TV Shows feature with pagination and details
- [x] Room database for offline movie storage
- [x] Material 3 design system implementation
- [x] Hebrew translations support
- [x] Cross-platform image loading with Coil
- [x] Reactive UI with StateFlow and Compose

### High Priority 🚀
- [ ] Fix Java compatibility issues (downgrade to JDK 17-21)
- [ ] Add TV Show database entities and local caching (currently only Movies cached)
- [ ] Implement comprehensive error handling with retry mechanisms
- [ ] Add pull-to-refresh functionality
- [ ] Create unit tests for ViewModels and repositories
- [ ] Add ktlint code formatting configuration

### Medium Priority 📋
- [ ] Implement search functionality across movies and TV shows
- [ ] Add favorites/watchlist feature with local storage
- [ ] Enhance loading states with skeleton screens
- [ ] Add more comprehensive movie/TV show details (reviews, videos)
- [ ] Implement network connectivity detection
- [ ] Add proguard/R8 configuration for release builds

### Low Priority 💡
- [ ] Add dark/light theme toggle
- [ ] Implement offline-first architecture with better sync strategies
- [ ] Add user authentication and personalized recommendations
- [ ] Performance optimizations for large datasets
- [ ] Add accessibility improvements
- [ ] Implement deep linking support
- [ ] Add CI/CD pipeline with GitHub Actions