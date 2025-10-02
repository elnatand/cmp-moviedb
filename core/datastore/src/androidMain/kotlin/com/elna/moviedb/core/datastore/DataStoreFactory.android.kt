package com.elna.moviedb.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Creates a DataStore instance for Android platform.
 *
 * @param context Android application context
 * @return DataStore instance configured for Android
 */
fun createDataStore(context: Context): DataStore<Preferences> {
    return androidx.datastore.preferences.core.PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath.toPath()
        }
    )
}

/**
 * Platform-specific implementation for Android.
 * Note: This requires Context to be injected via Koin.
 */
actual fun createDataStore(): DataStore<Preferences> {
    error("Context is required for Android DataStore. Use createDataStore(context) instead.")
}
