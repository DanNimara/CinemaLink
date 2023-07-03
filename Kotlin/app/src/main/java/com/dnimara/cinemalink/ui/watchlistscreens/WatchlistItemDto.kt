package com.dnimara.cinemalink.ui.watchlistscreens

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchlistItemDto(val id: Long,
                            val name: String,
                            var isSelected: Boolean = false): Parcelable
