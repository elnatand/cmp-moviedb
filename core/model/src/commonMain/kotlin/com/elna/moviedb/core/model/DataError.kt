package com.elna.moviedb.core.model

/**
 * Coarse, presentation-agnostic classification of a failed operation.
 *
 * The data layer maps low-level failures (HTTP status, exceptions) to one of these
 * categories so the presentation layer can pick a localized, user-facing message
 * without ever parsing or displaying raw technical strings.
 */
enum class DataError {
    /** Connectivity / IO failure — typically "check your connection, try again". */
    NETWORK,

    /** Server-side failure (5xx). */
    SERVER,

    /** Client-side failure (4xx) — bad request, not found, unauthorized, etc. */
    CLIENT,

    /** Anything that doesn't fit the above (e.g. deserialization). */
    UNKNOWN,
}
