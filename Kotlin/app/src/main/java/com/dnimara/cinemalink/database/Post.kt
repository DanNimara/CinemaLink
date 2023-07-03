package com.dnimara.cinemalink.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dnimara.cinemalink.domain.FeedPost
import com.dnimara.cinemalink.domain.TagMovie

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val postId: Long,
    val userId: Long,
    val username: String,
    val postingTime: Long,
    val userProfilePicUrl: String,
    val content: String,
    val liked: Boolean?,
    val likes: Long,
    val dislikes: Long,
    val commentCount: Long,
    val edited: Boolean,
    val deleted: Boolean)

fun List<PostWithMovies>.asDomainModel(): List<FeedPost> {
    return map {
        FeedPost(
            postId = it.post.postId,
            userId = it.post.userId,
            username = it.post.username,
            postingTime = it.post.postingTime,
            userProfilePicUrl = it.post.userProfilePicUrl,
            content = it.post.content,
            liked = it.post.liked,
            likes = it.post.likes,
            dislikes = it.post.dislikes,
            edited = it.post.edited,
            deleted = it.post.deleted,
            commentCount = it.post.commentCount,
            tags = it.movies.map { mov ->
                TagMovie(mov.movieId, mov.title)
            }
        )
    }
}