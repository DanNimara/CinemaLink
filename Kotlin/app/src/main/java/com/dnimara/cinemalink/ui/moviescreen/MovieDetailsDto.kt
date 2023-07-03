package com.dnimara.cinemalink.ui.moviescreen

import android.os.Parcelable
import com.dnimara.cinemalink.ui.reviewscreen.ReviewDto
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistItemDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetailsDto(var id: String,
                           var title: String,
                           var duration: Long?,
                           var imdbRating: Double?,
                           var posterUrl: String?,
                           var plot: String?,
                           var releaseDay: Int?,
                           var releaseMonth: Int?,
                           var releaseYear: Int?,
                           var availableOnNetflix: Boolean?,
                           var availableOnHBOMax: Boolean?,
                           var availableOnDisneyPlus: Boolean?,
                           var availableOnAmazonPrime: Boolean?,
                           var internalRating: Double?,
                           var numberOfVotes: Int?,
                           var userRating: Double?,
                           var rating: String?,
                           var ratingReason: String?,
                           var runtimeInSeconds: Long?,
                           var tagline: String?,
                           var userReview: ReviewDto?,
                           var genres: List<String>?,
                           var crew: List<CrewDto>?,
                           var ownedStatuses: List<MovieCollectionStatusDto>,
                           var watchlists: List<WatchlistItemDto>): Parcelable
