package com.dnimara.cinemalink.ui.moviescreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCollectionStatusDto(val statusName: String,
                                    val owned: Boolean): Parcelable
