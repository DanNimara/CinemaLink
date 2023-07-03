package com.dnimara.cinemalink.database

import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
    val postId: Long,
    val userId: Long,
    val username: String,
    val postingTime: Long,
    val userProfilePicUrl: String,
    val content: String,
    val liked: Boolean?,
    val likes: Long,
    val edited: Boolean,
    val deleted: Boolean?,
    val dislikes: Long,
    val commentCount: Long,
    val tags: List<MovieSummaryDto>?
)

fun List<PostDto>.asDatabaseModel(): List<Post> {
    return map {
        Post(
            postId = it.postId,
            userId = it.userId,
            username = it.username,
            postingTime = it.postingTime,
            userProfilePicUrl = it.userProfilePicUrl,
            content = it.content,
            liked = it.liked,
            edited = it.edited,
            deleted = it.deleted!!,
            likes = it.likes,
            dislikes = it.dislikes,
            commentCount = it.commentCount
        )
    }
}