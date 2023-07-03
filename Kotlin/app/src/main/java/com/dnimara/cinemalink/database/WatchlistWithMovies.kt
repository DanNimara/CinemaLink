package com.dnimara.cinemalink.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WatchlistWithMovies(
    @Embedded val watchlist: Watchlist,
    @Relation(
        parentColumn = "watchlistId",
        entity = Movie::class,
        entityColumn = "movieId",
        associateBy = Junction(WatchlistMovieCrossRef::class)
    )
    val movies: List<Movie>
)