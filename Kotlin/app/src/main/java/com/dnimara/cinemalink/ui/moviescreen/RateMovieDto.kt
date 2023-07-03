package com.dnimara.cinemalink.ui.moviescreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RateMovieDto(val movieId: String, val rating: Double): Parcelable
