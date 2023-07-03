package com.dnimara.cinemalink.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PostWithMovies(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "postId",
        entity = Movie::class,
        entityColumn = "movieId",
        associateBy = Junction(PostMovieCrossRef::class)
    )
    val movies: List<Movie>
)
