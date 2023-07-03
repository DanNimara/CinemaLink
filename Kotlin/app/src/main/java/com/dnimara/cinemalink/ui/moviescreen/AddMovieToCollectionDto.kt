package com.dnimara.cinemalink.ui.moviescreen

data class AddMovieToCollectionDto(val movieId: String,
                                    val ownedStatusList: List<OwnedStatus>)
