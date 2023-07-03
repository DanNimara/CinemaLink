package com.dnimara.cinemalink.ui.feedscreen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieTagDto(val id: String,
                    val title: String,
                    var posterUrl: String?): Parcelable
