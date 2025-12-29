# Module Dependencies Graph

Last updated: 2025-12-29

This diagram shows the dependencies between modules in the CMP MovieDB project.

```mermaid
graph LR
    subgraph App["Application Layer"]
        composeApp[":composeApp"]
    end

    subgraph Features["Feature Modules"]
        featuresmovies["movies"]
        featuresperson["person"]
        featuresprofile["profile"]
        featuressearch["search"]
        featurestvshows["tv-shows"]
    end

    subgraph Core["Core Modules"]
        corecommon["common"]
        coredata["data"]
        coredatabase["database"]
        coredatastore["datastore"]
        coremodel["model"]
        corenetwork["network"]
        coreui["ui"]
    end

    %% Dependencies
    %% :composeApp dependencies
    composeApp --> corecommon
    composeApp --> coremodel
    composeApp --> corenetwork
    composeApp --> coredata
    composeApp --> coredatabase
    composeApp --> coredatastore
    composeApp --> coreui
    composeApp --> featuresmovies
    composeApp --> featurestvshows
    composeApp --> featuressearch
    composeApp --> featuresprofile
    composeApp --> featuresperson

    %% :features:movies dependencies
    featuresmovies --> coremodel
    featuresmovies --> coredata
    featuresmovies --> coreui

    %% :features:person dependencies
    featuresperson --> coremodel
    featuresperson --> coredata
    featuresperson --> coreui

    %% :features:profile dependencies
    featuresprofile --> coremodel
    featuresprofile --> coredata
    featuresprofile --> coredatastore
    featuresprofile --> corecommon
    featuresprofile --> coreui

    %% :features:search dependencies
    featuressearch --> coremodel
    featuressearch --> coredata
    featuressearch --> coreui

    %% :features:tv-shows dependencies
    featurestvshows --> coremodel
    featurestvshows --> coredata
    featurestvshows --> coreui

    %% :core:data dependencies
    coredata --> coremodel
    coredata --> corecommon
    coredata --> corenetwork
    coredata --> coredatabase
    coredata --> coredatastore

    %% :core:database dependencies
    coredatabase --> coremodel
    coredatabase --> corecommon

    %% :core:datastore dependencies
    coredatastore --> coremodel

    %% :core:network dependencies
    corenetwork --> coremodel
    corenetwork --> corecommon

    %% Styling
    classDef appStyle fill:#FFF9C4,stroke:#F57C00,stroke-width:2px
    classDef featureStyle fill:#C8E6C9,stroke:#388E3C,stroke-width:2px
    classDef coreStyle fill:#BBDEFB,stroke:#1976D2,stroke-width:2px

    class composeApp appStyle
    class featuresmovies,featuresperson,featuresprofile,featuressearch,featurestvshows featureStyle
    class corecommon,coredata,coredatabase,coredatastore,coremodel,corenetwork,coreui coreStyle
```

## Legend

- **App Layer** (Yellow): Main application module
- **Feature Modules** (Green): User-facing features with UI
- **Core Modules** (Blue): Shared infrastructure and business logic

## Module Descriptions

### Application Layer
- **composeApp**: Main application module with navigation and DI setup

### Feature Modules
- **movies**: Movies list and details screens
- **tv-shows**: TV shows list and details screens
- **search**: Multi-type search functionality
- **profile**: User profile and settings
- **person**: Cast/crew details screen

### Core Modules
- **data**: Repository implementations
- **ui**: Shared UI components and theme
- **network**: HTTP client and API definitions
- **database**: Room database for offline storage
- **datastore**: Preferences and state persistence
- **model**: Domain models and DTOs
- **common**: Common utilities and dispatchers
