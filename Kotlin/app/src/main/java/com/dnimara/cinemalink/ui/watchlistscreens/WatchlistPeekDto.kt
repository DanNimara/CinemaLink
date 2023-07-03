package com.dnimara.cinemalink.ui.watchlistscreens

import android.os.Parcelable
import com.dnimara.cinemalink.database.Post
import com.dnimara.cinemalink.database.PostDto
import com.dnimara.cinemalink.database.Watchlist
import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchlistPeekDto(val id: Long,
                    val name: String,
                    val createdTime: Long,
                    val watchlistStatus: String,
                    val movieCount: Int,
                    val movies: List<MovieSummaryDto>?): Parcelable

fun List<WatchlistPeekDto>.asDatabaseModel(): List<Watchlist> {
    return map {
        Watchlist(
            watchlistId = it.id,
            name = it.name,
            createdTime = it.createdTime,
            watchlistStatus = it.watchlistStatus,
            movieCount = it.movieCount
        )
    }
}