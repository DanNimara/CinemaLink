package com.dnimara.cinemalink.ui.moviescreen

import android.os.Parcelable
import com.dnimara.cinemalink.database.Movie
import com.dnimara.cinemalink.ui.reviewscreen.ReviewDto
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieSummaryDto(var id: String,
                           var title: String,
                           var posterUrl: String?,
                           val outline: String?,
                           val releaseDay: Int?,
                           val releaseMonth: Int?,
                           val releaseYear: Int?,
                           val duration: Long?,
                           val internalRating: Double?,
                           val imdbRating: Double?,
                           val note: String?,
                           val addedTime: Long?,
                           val review: ReviewDto?) : Parcelable


fun List<MovieSummaryDto>.asDatabaseModel(): List<Movie> {
    return map {
        Movie(
            movieId = it.id,
            title = it.title,
            posterUrl = it.posterUrl,
            outline = it.outline,
            releaseDay = it.releaseDay,
            releaseMonth = it.releaseMonth,
            releaseYear = it.releaseYear,
            duration = it.duration,
            internalRating = it.internalRating,
            imdbRating = it.imdbRating
        )
    }
}