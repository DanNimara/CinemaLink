package com.dnimara.cinemalink.ui.watchlistscreens

data class WatchlistDto(val id: Long,
                        val name: String,
                        val createdTime: Long,
                        val watchlistStatus: String,
                        val movieCount: Int)