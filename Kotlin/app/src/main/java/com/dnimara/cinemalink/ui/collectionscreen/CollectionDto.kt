package com.dnimara.cinemalink.ui.collectionscreen

import android.os.Parcelable
import com.dnimara.cinemalink.ui.moviescreen.OwnedStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollectionDto(val id: Long,
                        val movies: List<CollectionMovieDto>):Parcelable

@Parcelize
data class CollectionMovieDto(var id: String,
                              var title: String,
                              var posterUrl: String?,
                              val outline: String?,
                              val duration: Long?,
                              val releaseDay: Int?,
                              val releaseMonth: Int?,
                              val releaseYear: Int?,
                              val addedTime: Long?,
                              val platforms: List<OwnedStatus>,
                              val genres: List<String>?): Parcelable