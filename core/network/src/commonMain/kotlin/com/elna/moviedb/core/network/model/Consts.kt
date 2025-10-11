package com.elna.moviedb.core.network.model

const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

// Using w500 for high-quality images on high-density displays
// Optimal for 140dp wide cards (140dp × 3 density = 420px, w500 provides 500px)
const val TMDB_IMAGE_URL = "https://media.themoviedb.org/t/p/w500"