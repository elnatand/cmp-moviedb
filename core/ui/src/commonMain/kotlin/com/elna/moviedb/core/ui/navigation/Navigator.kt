package com.elna.moviedb.core.ui.navigation

/**
 * Navigation intents emitted by screens, decoupled from whoever owns the back stack:
 * the Compose `NavDisplay` (Android and iOS < 26) or SwiftUI's NavigationStack
 * (iOS 26+ Liquid Glass shell).
 */
interface Navigator {

    /**
     * Pushes [route]. [title] is a display title for hosts with a native toolbar
     * (empty when unknown); back-stack-owning hosts ignore it.
     */
    fun navigate(route: Route, title: String = "")

    /** Pops the current screen. */
    fun goBack()
}
