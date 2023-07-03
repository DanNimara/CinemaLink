package com.dnimara.cinemalink.database

import androidx.room.Entity

@Entity(tableName = "watchlist_movie_join",
    primaryKeys = ["watchlistId", "movieId"])
data class WatchlistMovieCrossRef(val watchlistId: Long,
                                val movieId: String,
                                val note: String?,
                                val addedTime: Long?)