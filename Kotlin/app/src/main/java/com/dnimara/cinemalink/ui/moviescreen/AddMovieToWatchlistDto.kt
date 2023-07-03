package com.dnimara.cinemalink.ui.moviescreen

data class AddMovieToWatchlistDto(val movieId: String,
                                  val watchlistId: Long,
                                  val note: String?)
