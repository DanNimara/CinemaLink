package com.dnimara.cinemalink.ui.feedscreen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostCommentsDto (val postId: Long,
                            val userId: Long,
                            val username: String,
                            val postingTime: Long,
                            val userProfilePicUrl: String,
                            val content: String,
                            val liked: Boolean?,
                            val likes: Long,
                            val edited: Boolean,
                            val dislikes: Long,
                            val commentCount: Long,
                            val tags: List<TaggedMovie>?,
                            val comments: List<CommentDto>?): Parcelable


@Parcelize
data class TaggedMovie(val id: String,
                    val title: String): Parcelable

@Parcelize
data class CommentDto(val id: Long,
                      val userId: Long,
                      val username: String,
                      val commentTime: Long,
                      val userProfilePicUrl: String,
                      val content: String,
                      val liked: Boolean?,
                      val likes: Long,
                      val dislikes: Long,
                      val edited: Boolean): Parcelable

data class EditDto(val content: String)