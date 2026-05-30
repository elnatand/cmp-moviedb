package com.elna.moviedb.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Lifecycle-aware collector for one-time UI events (snackbars, navigation, …).
 *
 * Unlike a bare `LaunchedEffect(Unit) { flow.collect { } }`, collection is suspended while the
 * screen is below [Lifecycle.State.STARTED] (e.g. backgrounded), so events aren't processed for
 * a screen the user can't see. Collection runs on [Dispatchers.Main.immediate] so an event
 * emitted from a main-thread coroutine is delivered without a dispatch hop.
 *
 * @param key an optional extra restart key; the effect also restarts if [events] or the
 *   lifecycle owner changes.
 */
@Composable
fun <T> ObserveAsEvents(events: Flow<T>, key: Any? = null, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(events, lifecycleOwner, key) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect(onEvent)
            }
        }
    }
}
