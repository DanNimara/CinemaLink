package com.dnimara.cinemalink.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FeedPost(val postId: Long,
                    val userId: Long,
                    val username: String,
                    val postingTime: Long,
                    val userProfilePicUrl: String,
                    val content: String,
                    val liked: Boolean?,
                    val likes: Long,
                    val dislikes: Long,
                    val edited: Boolean,
                    val deleted: Boolean,
                    val commentCount: Long,
                    val tags: List<TagMovie>?)

data class TagMovie(val movieId: String,
                    val title: String)

data class ShareDto(val username: String,
                    val content: String,
                    val reference: String)