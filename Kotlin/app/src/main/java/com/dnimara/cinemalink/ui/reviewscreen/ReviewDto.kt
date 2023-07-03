package com.dnimara.cinemalink.ui.reviewscreen

import android.os.Parcelable
import com.dnimara.cinemalink.ui.feedscreen.CommentDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewDto(val id: Long,
                     val userId: Long,
                     val username: String,
                     val userProfilePicUrl: String,
                     val reviewTime: Long,
                     val content: String,
                     val recommended: Boolean,
                     val liked: Boolean?,
                     val movieId: String,
                     val movieTitle: String,
                     val likes: Long,
                     val dislikes: Long,
                     val commentCount: Long,
                     val rating: Double?): Parcelable

@Parcelize
data class ReviewCommentsDto(val id: Long,
                            val userId: Long,
                            val username: String,
                            val userProfilePicUrl: String,
                            val reviewTime: Long,
                            val content: String,
                            val recommended: Boolean,
                            val liked: Boolean?,
                            val movieId: String,
                            val movieTitle: String,
                            val rating: Double?,
                            val likes: Long,
                            val dislikes: Long,
                            val commentCount: Long,
                            val comments: List<CommentDto>?): Parcelable