package com.dnimara.cinemalink.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dnimara.cinemalink.domain.FeedPost
import com.dnimara.cinemalink.domain.TagMovie
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistDto
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistPeekDto

@Entity(tableName = "watchlists")
data class Watchlist(
    @PrimaryKey
    val watchlistId: Long,
    val name: String,
    val createdTime: Long,
    val watchlistStatus: String,
    val movieCount: Int
)

fun List<Watchlist>.asDomainModdel(): List<WatchlistDto> {
    return map {
        WatchlistDto(
           id = it.watchlistId,
           name = it.name,
           createdTime = it.createdTime,
           watchlistStatus = it.watchlistStatus,
           movieCount = it.movieCount
        )
    }
}