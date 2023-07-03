package com.dnimara.cinemalink.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "post_movie_join",
    primaryKeys = ["postId", "movieId"])
data class PostMovieCrossRef(val postId: Long,
                            val movieId: String)
