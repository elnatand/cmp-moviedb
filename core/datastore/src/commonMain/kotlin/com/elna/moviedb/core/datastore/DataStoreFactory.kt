package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Creates a DataStore instance for the current platform.
 * The file name for the preferences datastore.
 */
internal const val DATA_STORE_FILE_NAME = "moviedb.preferences_pb"

/**
 * Factory function to create a platform-specific DataStore instance.
 * Each platform must provide its own implementation of this function.
 */
expect fun createDataStore(): DataStore<Preferences>
