package com.dnimara.cinemalink.ui.moviescreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CrewDto(var id: String,
                var name: String,
                var category: String): Parcelable
