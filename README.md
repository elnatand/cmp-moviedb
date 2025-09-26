# CMP MovieDB

A Kotlin Multiplatform Mobile (KMP) application built with Compose Multiplatform that displays movies and TV shows information. The app targets both Android and iOS platforms with shared business logic and UI components.

## 🏗️ Architecture

The project follows Clean Architecture principles with a modular approach:

- **MVVM Pattern**: ViewModels handle UI state and business logic
- **Repository Pattern**: Data layer abstraction
- **Dependency Injection**: Koin for DI across modules
- **Compose Multiplatform**: Shared UI components
- **Room Database**: Local database management

## 📱 Features

- **Movies**: Browse and view movie details with infinite scroll pagination
- **TV Shows**: Explore TV shows with detailed information
- **Profile**: User profile management
- **Offline Support**: Local database caching with page-based data storage
- **Cross-platform**: Shared codebase for Android and iOS
- **Error Handling**: Top-positioned SnackBar notifications for network errors
- **Auto-pagination**: Automatic loading of next movie pages when scrolling to bottom

## 📁 Project Structure

```
cmp-moviedb/
├── composeApp/                     # Main application module
│   ├── src/
│   │   ├── androidMain/           # Android-specific code
│   │   ├── commonMain/            # Shared code across platforms
│   │   └── iosMain/               # iOS-specific code
│   └── build.gradle.kts
│
├── core/                          # Core modules
│   ├── common/                    # Common utilities and dispatchers
│   │   └── src/commonMain/kotlin/
│   │       ├── di/                # Common DI module
│   │       └── AppDispatchers.kt  # Coroutine dispatcher configurations
│   │
│   ├── data/                      # Data layer
│   │   └── src/commonMain/kotlin/
│   │       ├── di/                # Data module DI
│   │       ├── movies/            # Movies data sources & repository
│   │       ├── tv_shows/          # TV shows data sources & repository
│   │       └── network/           # Network configuration
│   │
│   ├── database/                  # Local database
│   │   └── src/
│   │       ├── commonMain/        # Room database setup
│   │       ├── androidMain/       # Android database driver
│   │       └── iosMain/           # iOS database driver
│   │
│   ├── model/                     # Data models
│   │   └── src/commonMain/kotlin/
│   │       └── com/example/moviedb/core/model/
│   │           ├── Movie.kt
│   │           ├── TvShow.kt
│   │           └── UiState.kt
│   │
│   └── ui/                        # Shared UI components
│       └── src/
│           ├── commonMain/        # Common UI components
│           ├── androidMain/       # Android-specific UI utilities
│           └── iosMain/           # iOS-specific UI utilities
│
├── features/                      # Feature modules
│   ├── movies/                    # Movies feature
│   │   └── src/commonMain/kotlin/
│   │       ├── di/                # Movies DI module
│   │       ├── navigation/        # Movies navigation
│   │       └── ui/
│   │           ├── movies/        # Movies list screen
│   │           └── movie_details/ # Movie details screen
│   │
│   ├── tv-shows/                  # TV Shows feature
│   │   └── src/commonMain/kotlin/
│   │       ├── di/                # TV Shows DI module
│   │       ├── navigation/        # TV Shows navigation
│   │       └── ui/
│   │           ├── tv_shows/      # TV shows list screen
│   │           └── tv_show_details/ # TV show details screen
│   │
│   └── profile/                   # Profile feature
│       └── src/commonMain/kotlin/
│           ├── di/                # Profile DI module
│           ├── navigation/        # Profile navigation
│           └── ui/                # Profile screen
│
├── build-logic/                   # Custom Gradle plugins
│   └── convention/
│       └── src/main/kotlin/
│           └── com/example/moviedb/
│               ├── ProjectExtensions.kt
│               └── Versions.kt
│
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
└── gradle.properties            # Gradle properties
```

## 🛠️ Technology Stack

### Shared
- **Kotlin Multiplatform Mobile (KMM)**
- **Compose Multiplatform** - UI framework
- **Koin** - Dependency injection
- **Room** - Local database with page-based data storage
- **Ktor** - Networking (implied from data layer)
- **Kotlin Coroutines** - Asynchronous programming
- **LazyVerticalGrid** - Grid-based UI with scroll detection
- **Material3 SnackBar** - Error notifications and user feedback

### Android
- **Jetpack Compose** - UI toolkit
- **Activity Compose** - Activity integration

### iOS
- **SwiftUI Integration** - iOS native integration

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Xcode 13+ (for iOS development)
- JDK 21
- Kotlin Multiplatform Mobile plugin
- TMDB API Key (see setup instructions below)

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
- **core:common** - Common utilities, coroutine dispatchers, and shared DI configuration
- **core:data** - Repository implementations, data sources, and network setup
- **core:database** - Room database configuration and platform drivers
- **core:model** - Shared data models and UI state definitions
- **core:ui** - Common UI components and platform-specific utilities

### Feature Modules
- **features:movies** - Complete movies feature with list and details
- **features:tv-shows** - TV shows browsing and details functionality
- **features:profile** - User profile management

### App Module
- **composeApp** - Main application module with navigation and DI setup

## 🏛️ Architecture Layers

1. **Presentation Layer** - Compose UI, ViewModels, Navigation
2. **Domain Layer** - Business logic, Use cases (Repository interfaces)
3. **Data Layer** - Repository implementations, Data sources, Network, Database

## 🔧 Development Setup

The project uses Gradle version catalogs and custom convention plugins for dependency management. Check `build-logic/` for shared build configuration.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
f
## TODOs
- Network Error and Exceptions
- Movie and TV Show details
- Implement pagination for TV Shows feature
- Add unit tests for pagination logic
- Implement error retry mechanism