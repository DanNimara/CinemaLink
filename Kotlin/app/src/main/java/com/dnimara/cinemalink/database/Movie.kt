package com.dnimara.cinemalink.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val movieId: String,
    val title: String,
    val posterUrl: String?,
    val outline: String?,
    val releaseDay: Int?,
    val releaseMonth: Int?,
    val releaseYear: Int?,
    val duration: Long?,
    val internalRating: Double?,
    val imdbRating: Double?
)
